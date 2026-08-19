import {
  Component,
  EventEmitter,
  Input,
  OnChanges,
  Output,
  SimpleChanges,
  inject
} from '@angular/core';

import {
  FormBuilder,
  ReactiveFormsModule,
  Validators
} from '@angular/forms';

import { CourseDto } from '../../../core/api/models/courseDto';
import { CourseControllerService } from '../../../core/api/services/courseController.service';

import { InputText } from 'primeng/inputtext';
import { InputNumber } from 'primeng/inputnumber';
import { Button } from 'primeng/button';

@Component({
  selector: 'app-course-form',
  imports: [
    ReactiveFormsModule,
    InputText,
    InputNumber,
    Button
  ],
  templateUrl: './course-form.html',
  styleUrl: './course-form.css'
})
export class CourseForm implements OnChanges {

  private readonly formBuilder = inject(FormBuilder);
  private readonly courseService = inject(CourseControllerService);

  @Input() course: CourseDto | null = null;

  @Output() saved = new EventEmitter<void>();

  @Output() cancelled = new EventEmitter<void>();

  form = this.formBuilder.nonNullable.group({
    courseName: ['', Validators.required],
    teacherId: ['', Validators.required],
    syllabus: [''],
    credits: [0, [Validators.required, Validators.min(1)]],
    description: ['']
  });

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['course']) {
      this.updateForm();
    }
  }

  updateForm(): void {
    if (this.course) {
      this.form.patchValue({
        courseName: this.course.courseName ?? '',
        teacherId: this.course.teacherId ?? '',
        syllabus: this.course.syllabus ?? '',
        credits: this.course.credits ?? 0,
        description: this.course.description ?? ''
      });
    } else {
      this.form.reset({
        courseName: '',
        teacherId: '',
        syllabus: '',
        credits: 0,
        description: ''
      });
    }
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const course: CourseDto = this.form.getRawValue();

    if (this.course?.id) {
      this.courseService.updateCourse(this.course.id, course).subscribe({
        next: () => this.saved.emit(),
        error: (error) => {
          console.error('Failed to update course', error);
        }
      });
    } else {
      this.courseService.createCourse(course).subscribe({
        next: () => this.saved.emit(),
        error: (error) => {
          console.error('Failed to create course', error);
        }
      });
    }
  }

  cancel(): void {
    this.cancelled.emit();
  }
}
