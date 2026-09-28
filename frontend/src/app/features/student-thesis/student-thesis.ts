import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { Button } from 'primeng/button';
import { Card } from 'primeng/card';
import { Tag } from 'primeng/tag';
import { Message } from 'primeng/message';
import { Dialog } from 'primeng/dialog';
import { MultiSelect } from 'primeng/multiselect';
import { Textarea } from 'primeng/textarea';
import { Toast } from 'primeng/toast';
import { MessageService } from 'primeng/api';

import { ThesisDto } from '../../core/api/models/thesisDto';
import { TeacherDto } from '../../core/api/models/teacherDto';
import { AdvisorRequestDto } from '../../core/api/models/advisorRequestDto';
import { ThesisControllerService } from '../../core/api/services/thesisController.service';
import { TeacherControllerService } from '../../core/api/services/teacherController.service';
import { AdvisorRequestControllerService } from '../../core/api/services/advisorRequestController.service';
import { AuthService } from '../../core/services/auth.service';
import { TRAIT_OPTIONS, traitLabel } from '../../shared/models/traits';

@Component({
  selector: 'app-student-thesis',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    Button,
    Card,
    Tag,
    Message,
    Dialog,
    MultiSelect,
    Textarea,
    Toast,
  ],
  templateUrl: './student-thesis.html',
  styleUrl: './student-thesis.css',
  providers: [MessageService],
})
export class StudentThesis implements OnInit {
  private readonly thesisService = inject(ThesisControllerService);
  private readonly teacherService = inject(TeacherControllerService);
  private readonly advisorService = inject(AdvisorRequestControllerService);
  private readonly messageService = inject(MessageService);
  private readonly auth = inject(AuthService);

  private readonly MAX_PENDING = 3;

  private readonly studentId = this.auth.getUserId();

  protected readonly thesis = signal<ThesisDto | null>(null);
  protected readonly loading = signal(true);
  protected readonly uploading = signal(false);
  protected readonly errorMessage = signal<string | null>(null);

  protected selectedFile: File | null = null;

  protected readonly requests = signal<AdvisorRequestDto[]>([]);
  protected readonly teachers = signal<TeacherDto[]>([]);

  protected readonly teachersById = computed(() => {
    const map = new Map<string, TeacherDto>();
    for (const teacher of this.teachers()) {
      if (teacher.id) {
        map.set(teacher.id, teacher);
      }
    }
    return map;
  });

  protected readonly advisorRequest = computed(
    () => this.requests().find((r) => r.status === 'APPROVED') ?? null,
  );
  protected readonly hasAdvisor = computed(() => this.advisorRequest() !== null);

  protected readonly pendingCount = computed(
    () => this.requests().filter((r) => r.status === 'PENDING').length,
  );

  protected readonly canRequest = computed(
    () => !this.hasAdvisor() && this.pendingCount() < this.MAX_PENDING,
  );

  protected readonly requestBlockedReason = computed(() => {
    if (this.hasAdvisor()) {
      return 'You already have an advisor.';
    }
    if (this.pendingCount() >= this.MAX_PENDING) {
      return `You have reached the maximum of ${this.MAX_PENDING} pending requests.`;
    }
    return null;
  });

  protected readonly requestedTeacherIds = computed(() => {
    const ids = new Set<string>();
    for (const request of this.requests()) {
      if (request.teacherId) {
        ids.add(request.teacherId);
      }
    }
    return ids;
  });

  protected readonly findDialogVisible = signal(false);
  protected readonly traitOptions = TRAIT_OPTIONS;
  protected selectedTraits: TeacherDto.TraitsEnum[] = [];
  protected readonly searching = signal(false);

  protected readonly searchResults = signal<TeacherDto[] | null>(null);
  protected readonly selectedTeacher = signal<TeacherDto | null>(null);
  protected requestMessage = '';
  protected readonly applying = signal(false);

  ngOnInit(): void {
    this.loadThesis();
    this.loadTeachers();
    this.loadRequests();
  }

  protected onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;

    if (!input.files || input.files.length === 0) {
      this.selectedFile = null;
      return;
    }

    const file = input.files[0];

    if (file.type !== 'application/pdf') {
      this.errorMessage.set('Please select a PDF file.');
      this.selectedFile = null;
      input.value = '';
      return;
    }

    this.errorMessage.set(null);
    this.selectedFile = file;
  }

  protected upload(): void {
    if (!this.selectedFile) {
      this.errorMessage.set('Please select a PDF file first.');
      return;
    }

    this.uploading.set(true);
    this.errorMessage.set(null);

    this.thesisService.uploadThesis(this.studentId, this.selectedFile).subscribe({
      next: (thesis) => {
        this.thesis.set(thesis);
        this.selectedFile = null;
        this.uploading.set(false);
      },
      error: (error) => {
        console.error('Failed to upload thesis', error);
        this.errorMessage.set('The thesis could not be uploaded. Please try again.');
        this.uploading.set(false);
      },
    });
  }

  protected loadThesis(): void {
    this.loading.set(true);
    this.errorMessage.set(null);

    this.thesisService.getThesisByStudent(this.studentId).subscribe({
      next: (thesis) => {
        this.thesis.set(thesis);
        this.loading.set(false);
      },
      error: (error) => {
        if (error.status === 404) {
          this.thesis.set(null);
        } else {
          console.error('Failed to load thesis', error);
          this.errorMessage.set('The thesis information could not be loaded.');
        }

        this.loading.set(false);
      },
    });
  }

  protected getStatusLabel(status: ThesisDto['status']): string {
    switch (status) {
      case 'CHECKED':
        return 'Checked';
      case 'REJECTED':
        return 'Rejected';
      case 'UNCHECKED':
      default:
        return 'Unchecked';
    }
  }

  protected getStatusSeverity(
    status: ThesisDto['status'],
  ): 'success' | 'warn' | 'danger' {
    switch (status) {
      case 'CHECKED':
        return 'success';
      case 'REJECTED':
        return 'danger';
      case 'UNCHECKED':
      default:
        return 'warn';
    }
  }

  protected loadTeachers(): void {
    this.teacherService.getAllTeachers().subscribe({
      next: (teachers) => this.teachers.set(teachers),
      error: () => this.teachers.set([]),
    });
  }

  protected loadRequests(): void {
    this.advisorService.getRequestsByStudent(this.studentId).subscribe({
      next: (requests) => this.requests.set(requests),
      error: () => this.requests.set([]),
    });
  }

  protected teacherName(teacherId: string | undefined): string {
    if (!teacherId) {
      return 'Unknown teacher';
    }

    const teacher = this.teachersById().get(teacherId);

    if (!teacher) {
      return 'Unknown teacher';
    }

    return `${teacher.title ?? ''} ${teacher.firstName ?? ''} ${teacher.lastName ?? ''}`.trim();
  }

  protected traitLabel(value: string): string {
    return traitLabel(value);
  }

  protected openFindDialog(): void {
    this.selectedTraits = [];
    this.searchResults.set(null);
    this.selectedTeacher.set(null);
    this.requestMessage = '';
    this.findDialogVisible.set(true);
  }

  protected onFindDialogVisibleChange(visible: boolean): void {
    if (!visible) {
      this.findDialogVisible.set(false);
    }
  }

  protected search(): void {
    if (this.selectedTraits.length === 0) {
      return;
    }

    this.searching.set(true);
    this.selectedTeacher.set(null);
    this.searchResults.set(null);

    this.teacherService.searchTeachersByTraits(this.selectedTraits).subscribe({
      next: (teachers) => {
        this.searchResults.set(teachers);
        this.searching.set(false);
      },
      error: () => {
        this.searchResults.set([]);
        this.searching.set(false);
        this.showError('The search could not be completed. Please try again.');
      },
    });
  }

  protected isAlreadyRequested(teacher: TeacherDto): boolean {
    return teacher.id ? this.requestedTeacherIds().has(teacher.id) : false;
  }

  protected selectTeacher(teacher: TeacherDto): void {
    if (this.isAlreadyRequested(teacher)) {
      return;
    }

    this.selectedTeacher.set(teacher);
  }

  protected sendRequest(): void {
    const teacher = this.selectedTeacher();

    if (!teacher?.id) {
      return;
    }

    this.applying.set(true);

    const request: AdvisorRequestDto = {
      studentId: this.studentId,
      teacherId: teacher.id,
      message: this.requestMessage.trim(),
    };

    this.advisorService.applyForAdvisor(request).subscribe({
      next: () => {
        this.applying.set(false);
        this.findDialogVisible.set(false);
        this.loadRequests();
        this.messageService.add({
          severity: 'success',
          summary: 'Request sent',
          detail: `Your request to ${this.teacherName(teacher.id)} was sent.`,
        });
      },
      error: (error) => {
        this.applying.set(false);
        this.showError(this.extractErrorMessage(error));
      },
    });
  }

  protected getRequestStatusLabel(status: AdvisorRequestDto['status']): string {
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

  protected getRequestStatusSeverity(
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

  private extractErrorMessage(error: unknown): string {
    const httpError = error as { error?: { message?: string }; message?: string };
    return (
      httpError?.error?.message ??
      httpError?.message ??
      'The request could not be sent. Please try again.'
    );
  }

  private showError(detail: string): void {
    this.messageService.add({ severity: 'error', summary: 'Error', detail });
  }
}
