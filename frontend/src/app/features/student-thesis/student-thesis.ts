import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';

import { Button } from 'primeng/button';
import { Card } from 'primeng/card';
import { Tag } from 'primeng/tag';
import { Message } from 'primeng/message';

import { ThesisDto } from '../../core/api/models/thesisDto';
import { ThesisControllerService } from '../../core/api/services/thesisController.service';

@Component({
  selector: 'app-student-thesis',
  standalone: true,
  imports: [CommonModule, Button, Card, Tag, Message],
  templateUrl: './student-thesis.html',
  styleUrl: './student-thesis.css',
})
export class StudentThesis implements OnInit {
  private readonly thesisService = inject(ThesisControllerService);

  protected readonly thesis = signal<ThesisDto | null>(null);
  protected readonly loading = signal(true);
  protected readonly uploading = signal(false);
  protected readonly errorMessage = signal<string | null>(null);

  protected selectedFile: File | null = null;

  private readonly studentId =
    'REPLACE_WITH_STUDENT_ID';

  ngOnInit(): void {
    this.loadThesis();
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

    this.thesisService
      .uploadThesis(this.studentId, this.selectedFile)
      .subscribe({
          next: thesis => {
            console.log('UPLOAD RESPONSE:', thesis);
            console.log('FILE NAME:', thesis.fileName);

            this.thesis.set(thesis);
            this.selectedFile = null;
            this.uploading.set(false);
        },
        error: error => {
          console.error('Failed to upload thesis', error);
          console.error('Backend error:', error.error);

          this.errorMessage.set(
            'The thesis could not be uploaded. Please try again.'
          );

          this.uploading.set(false);
        },
      });
  }

  protected loadThesis(): void {
    this.loading.set(true);
    this.errorMessage.set(null);

    this.thesisService.getThesisByStudent(this.studentId).subscribe({
      next: thesis => {
        this.thesis.set(thesis);
        this.loading.set(false);
      },
      error: error => {
        if (error.status === 404) {
          this.thesis.set(null);
        } else {
          console.error('Failed to load thesis', error);

          this.errorMessage.set(
            'The thesis information could not be loaded.'
          );
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
    status: ThesisDto['status']
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
}
