import {Component, computed, inject, OnInit, signal} from '@angular/core';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MessageService } from 'primeng/api';
import { Button } from 'primeng/button';
import { Dialog } from 'primeng/dialog';
import { InputText } from 'primeng/inputtext';
import { Select } from 'primeng/select';
import { DatePicker } from 'primeng/datepicker';
import { InputNumber } from 'primeng/inputnumber';
import { Table, TableModule } from 'primeng/table';
import { Toast } from 'primeng/toast';

import { ExamControllerService } from '../../core/api/api/examController.service';
import { ExamDto } from '../../core/api/model/examDto';
import {CourseControllerService, CourseDto} from '../../core/api';

@Component({
  selector: 'app-exams',
  imports: [Button, Dialog, InputText, Select, DatePicker, InputNumber, ReactiveFormsModule, TableModule, Toast],
  templateUrl: './exams.html',
  styleUrl: './exams.css',
  providers: [MessageService],
})
export class Exams implements OnInit {
  private readonly examService = inject(ExamControllerService);
  private readonly messageService = inject(MessageService);
  private readonly formBuilder = inject(NonNullableFormBuilder);
  private readonly courseService = inject(CourseControllerService);
  private readonly currentTeacherId = signal<string | null>(null);/// de modificat dupa auth

  protected readonly exams = signal<ExamDto[]>([]);
  protected readonly loading = signal(false);
  protected readonly formDialogVisible = signal(false);
  protected readonly courses = signal<CourseDto[]>([]);

  protected readonly examTypes = [
    { label: 'Partial', value: 'PARTIAL' },
    { label: 'Final', value: 'FINAL' },
    { label: 'Resit', value: 'RESIT' },
  ];
  protected readonly examFormats = [
    { label: 'Written', value: 'WRITTEN' },
    { label: 'Practical', value: 'PRACTICAL' },
  ];

  protected readonly form = this.formBuilder.group({
    courseId: ['', Validators.required],
    examType: ['PARTIAL', Validators.required],
    examFormat: ['WRITTEN', Validators.required],
    group: ['', Validators.required],
    room: ['', Validators.required],
    duration: [120, Validators.required],
    primaryDate: ['', Validators.required],
    secondaryDate: [''],
  });

  protected readonly teacherCourses = computed(() =>
    this.courses().filter(c => c.teacherId === this.currentTeacherId())
  );

  ngOnInit(): void {
    this.loadExams();
    this.loadCourses();
  }

  protected loadExams(): void {
    this.loading.set(true);
    this.examService.getAllExams().subscribe({
      next: (exams) => {
        this.exams.set(exams);
        this.loading.set(false);
      },
      error: () => {
        this.loading.set(false);
        this.showError('Could not load the exams.');
      },
    });
  }

  protected loadCourses(): void {
    //de modificat pt ca in alt PR e pus numele getAllCourses
    this.courseService.getAll3().subscribe({
      next: (courses) => this.courses.set(courses),
      error: () => this.showError('Could not load the courses.'),
    });
  }

  protected courseName(courseId: string): string {
    const course = this.courses().find(c => c.id === courseId);
    return course?.courseName ?? courseId;
  }

  protected openCreateDialog(): void {
    this.form.reset({
      courseId: '',
      examType: 'PARTIAL',
      examFormat: 'WRITTEN',
      group: '',
      room: '',
      duration: 120,
      primaryDate: '',
      secondaryDate: '',
    });
    this.formDialogVisible.set(true);
  }

  protected closeFormDialog(): void {
    this.formDialogVisible.set(false);
    this.form.reset();
  }

  protected save(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const examDto ={ ...this.form.getRawValue(), teacherId: this.currentTeacherId(), } as ExamDto;

    this.examService.createExam(examDto).subscribe({
      next: () => {
        this.closeFormDialog();
        this.loadExams();
        this.showSuccess('Exam scheduled');
      },
      error: (err) => {
        const message = err.error?.message ?? 'Could not schedule the exam.';
        this.showError(message);
      },
    });
  }

  private showSuccess(summary: string): void {
    this.messageService.add({ severity: 'success', summary });
  }

  private showError(detail: string): void {
    this.messageService.add({ severity: 'error', summary: 'Error', detail });
  }
}
