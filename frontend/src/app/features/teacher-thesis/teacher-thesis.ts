import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';

import { Button } from 'primeng/button';
import { Tag } from 'primeng/tag';
import { Toast } from 'primeng/toast';
import { ConfirmDialog } from 'primeng/confirmdialog';
import { TableModule } from 'primeng/table';
import { ConfirmationService, MessageService } from 'primeng/api';

import { StudentDto } from '../../core/api/models/studentDto';
import { AdvisorRequestDto } from '../../core/api/models/advisorRequestDto';
import { AdvisorRequestControllerService } from '../../core/api/services/advisorRequestController.service';
import { StudentControllerService } from '../../core/api/services/studentController.service';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-teacher-thesis',
  standalone: true,
  imports: [CommonModule, Button, Tag, Toast, ConfirmDialog, TableModule],
  templateUrl: './teacher-thesis.html',
  styleUrl: './teacher-thesis.css',
  providers: [ConfirmationService, MessageService],
})
export class TeacherThesis implements OnInit {
  private readonly advisorService = inject(AdvisorRequestControllerService);
  private readonly studentService = inject(StudentControllerService);
  private readonly confirmationService = inject(ConfirmationService);
  private readonly messageService = inject(MessageService);
  private readonly auth = inject(AuthService);

  private readonly teacherId = this.auth.getUserId();

  protected readonly requests = signal<AdvisorRequestDto[]>([]);
  protected readonly students = signal<StudentDto[]>([]);
  protected readonly loading = signal(true);
  protected readonly processingId = signal<string | null>(null);

  protected readonly studentsById = computed(() => {
    const map = new Map<string, StudentDto>();
    for (const student of this.students()) {
      if (student.id) {
        map.set(student.id, student);
      }
    }
    return map;
  });

  ngOnInit(): void {
    this.loadStudents();
    this.loadRequests();
  }

  protected loadStudents(): void {
    this.studentService.getAllStudents().subscribe({
      next: (students) => this.students.set(students),
      error: () => this.students.set([]),
    });
  }

  protected loadRequests(): void {
    this.loading.set(true);

    this.advisorService.getRequestsByTeacher(this.teacherId).subscribe({
      next: (requests) => {
        this.requests.set(requests);
        this.loading.set(false);
      },
      error: () => {
        this.requests.set([]);
        this.loading.set(false);
        this.showError('Could not load the advisor requests.');
      },
    });
  }

  protected studentName(studentId: string | undefined): string {
    if (!studentId) {
      return 'Unknown student';
    }

    const student = this.studentsById().get(studentId);

    if (!student) {
      return 'Unknown student';
    }

    const name = `${student.firstName ?? ''} ${student.lastName ?? ''}`.trim();
    return student.group ? `${name} (${student.group})` : name;
  }

  protected confirmApprove(request: AdvisorRequestDto): void {
    if (!request.id) {
      return;
    }

    this.confirmationService.confirm({
      header: 'Approve request',
      message: `Approve ${this.studentName(request.studentId)} as your student? Their other pending requests will be cancelled.`,
      icon: 'pi pi-check',
      acceptButtonProps: { label: 'Approve' },
      rejectButtonProps: { label: 'Cancel', severity: 'secondary', outlined: true },
      accept: () => this.approve(request.id!),
    });
  }

  protected confirmReject(request: AdvisorRequestDto): void {
    if (!request.id) {
      return;
    }

    this.confirmationService.confirm({
      header: 'Reject request',
      message: `Reject the request from ${this.studentName(request.studentId)}?`,
      icon: 'pi pi-times',
      acceptButtonProps: { label: 'Reject', severity: 'danger' },
      rejectButtonProps: { label: 'Cancel', severity: 'secondary', outlined: true },
      accept: () => this.reject(request.id!),
    });
  }

  private approve(id: string): void {
    this.processingId.set(id);

    this.advisorService.approveRequest(id).subscribe({
      next: () => {
        this.processingId.set(null);
        this.loadRequests();
        this.messageService.add({ severity: 'success', summary: 'Request approved' });
      },
      error: () => {
        this.processingId.set(null);
        this.showError('Could not approve the request.');
      },
    });
  }

  private reject(id: string): void {
    this.processingId.set(id);

    this.advisorService.rejectRequest(id).subscribe({
      next: () => {
        this.processingId.set(null);
        this.loadRequests();
        this.messageService.add({ severity: 'success', summary: 'Request rejected' });
      },
      error: () => {
        this.processingId.set(null);
        this.showError('Could not reject the request.');
      },
    });
  }

  protected getStatusLabel(status: AdvisorRequestDto['status']): string {
    switch (status) {
      case 'APPROVED':
        return 'Approved';
      case 'REJECTED':
        return 'Rejected';
      case 'CANCELLED':
        return 'Cancelled';
      case 'PENDING':
      default:
        return 'Pending';
    }
  }

  protected getStatusSeverity(
    status: AdvisorRequestDto['status'],
  ): 'success' | 'warn' | 'danger' | 'secondary' {
    switch (status) {
      case 'APPROVED':
        return 'success';
      case 'REJECTED':
        return 'danger';
      case 'CANCELLED':
        return 'secondary';
      case 'PENDING':
      default:
        return 'warn';
    }
  }

  private showError(detail: string): void {
    this.messageService.add({ severity: 'error', summary: 'Error', detail });
  }
}
