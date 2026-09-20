import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { TagModule } from 'primeng/tag';
import { MessageService } from 'primeng/api';
import { ToastModule } from 'primeng/toast';

import { GraduationThesisControllerService } from '../../core/api/services/graduationThesisController.service';
import { StudentControllerService } from '../../core/api/services/studentController.service';
import { GraduationThesisDto } from '../../core/api/models/graduationThesisDto';
import { StudentDto } from '../../core/api/models/studentDto';

@Component({
  selector: 'app-graduation-theses',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    TableModule,
    ButtonModule,
    TagModule,
    ToastModule
  ],
  providers: [MessageService],
  templateUrl: './graduation-theses.html',
  styleUrl: './graduation-theses.css'
})
export class GraduationTheses implements OnInit {

  // Temporary until authentication/current teacher is implemented.
  private readonly advisorId = '11111111-1111-1111-1111-111111111111';

  theses: GraduationThesisDto[] = [];
  students: StudentDto[] = [];

  loading = false;

  selectedThesis: GraduationThesisDto | null = null;

  thesisDialogVisible = false;
  rejectDialogVisible = false;
  messageDialogVisible = false;

  rejectionMessage = '';

  // Temporary frontend state until real file viewing/tracking is implemented.
  private readonly openedThesisIds = new Set<string>();

  constructor(
    private thesisService: GraduationThesisControllerService,
    private studentService: StudentControllerService,
    private messageService: MessageService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadStudents();
    this.loadTheses();
  }

  loadTheses(): void {
    this.loading = true;

    this.thesisService.getByAdvisorId(this.advisorId).subscribe({
      next: (theses) => {
        this.theses = theses;
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.loading = false;
        this.cdr.detectChanges();

        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: 'Could not load graduation theses.'
        });
      }
    });
  }

  loadStudents(): void {
    this.studentService.getAll().subscribe({
      next: (students) => {
        this.students = students;
        this.cdr.detectChanges();
      },
      error: () => {
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: 'Could not load students.'
        });
      }
    });
  }

  getStudentName(studentId?: string): string {
    if (!studentId) {
      return '-';
    }

    const student = this.students.find(item => item.id === studentId);

    if (!student) {
      return studentId;
    }

    return `${student.firstName ?? ''} ${student.lastName ?? ''}`.trim() || studentId;
  }

  openThesis(thesis: GraduationThesisDto): void {
    this.selectedThesis = thesis;

    if (thesis.id) {
      this.openedThesisIds.add(thesis.id);
    }

    this.thesisDialogVisible = true;
  }

  closeThesisDialog(): void {
    this.thesisDialogVisible = false;
    this.selectedThesis = null;
  }

  hasOpenedThesis(thesis: GraduationThesisDto): boolean {
    return !!thesis.id && this.openedThesisIds.has(thesis.id);
  }

  canReview(thesis: GraduationThesisDto): boolean {
    return thesis.status === 'UNCHECKED' && this.hasOpenedThesis(thesis);
  }

  admit(thesis: GraduationThesisDto): void {
    if (!thesis.id || !this.canReview(thesis)) {
      return;
    }

    this.thesisService.admit(thesis.id).subscribe({
      next: (updatedThesis) => {
        this.updateThesis(updatedThesis);
        this.cdr.detectChanges();

        this.messageService.add({
          severity: 'success',
          summary: 'Success',
          detail: 'The thesis was admitted.'
        });
      },
      error: () => {
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: 'The thesis could not be admitted.'
        });
      }
    });
  }

  openRejectDialog(thesis: GraduationThesisDto): void {
    if (!this.canReview(thesis)) {
      return;
    }

    this.selectedThesis = thesis;
    this.rejectionMessage = '';
    this.rejectDialogVisible = true;
  }

  reject(): void {
    if (
      !this.selectedThesis?.id ||
      !this.rejectionMessage.trim() ||
      !this.canReview(this.selectedThesis)
    ) {
      return;
    }

    this.thesisService
      .reject(this.selectedThesis.id, this.rejectionMessage.trim())
      .subscribe({
        next: (updatedThesis) => {
          this.updateThesis(updatedThesis);
          this.rejectDialogVisible = false;
          this.selectedThesis = null;
          this.rejectionMessage = '';
          this.cdr.detectChanges();

          this.messageService.add({
            severity: 'success',
            summary: 'Success',
            detail: 'The thesis was rejected.'
          });
        },
        error: () => {
          this.messageService.add({
            severity: 'error',
            summary: 'Error',
            detail: 'The thesis could not be rejected.'
          });
        }
      });
  }

  closeRejectDialog(): void {
    this.rejectDialogVisible = false;
    this.selectedThesis = null;
    this.rejectionMessage = '';
  }

  openMessageDialog(thesis: GraduationThesisDto): void {
    this.selectedThesis = thesis;
    this.messageDialogVisible = true;
  }

  closeMessageDialog(): void {
    this.messageDialogVisible = false;
    this.selectedThesis = null;
  }

  getRejectionMessage(): string {
    return this.selectedThesis?.rejectionMessage?.trim()
      || 'No rejection message was provided.';
  }

  private updateThesis(updatedThesis: GraduationThesisDto): void {
    this.theses = this.theses.map(thesis =>
      thesis.id === updatedThesis.id ? updatedThesis : thesis
    );
  }
}
