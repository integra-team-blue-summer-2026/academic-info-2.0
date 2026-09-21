import {
  ChangeDetectorRef,
  Component,
  OnInit
} from '@angular/core';

import { CourseDto } from '../../../core/api/models/courseDto';
import { CourseControllerService } from '../../../core/api/services/courseController.service';

import { TeacherAvailabilityControllerService } from '../../../core/api/services/teacherAvailabilityController.service';
import { TeacherAvailabilityDto } from '../../../core/api/models/teacherAvailabilityDto';

import {
  FormArray,
  FormBuilder,
  ReactiveFormsModule,
  Validators
} from '@angular/forms';

import { TableModule } from 'primeng/table';
import { CardModule } from 'primeng/card';
import { Button } from 'primeng/button';
import { Toast } from 'primeng/toast';
import { MessageService } from 'primeng/api';

import { forkJoin, Observable, of } from 'rxjs';

@Component({
  selector: 'app-teacher-overview',
  imports: [
    TableModule,
    CardModule,
    Button,
    Toast,
    ReactiveFormsModule
  ],
  templateUrl: './teacher-overview.html',
  styleUrl: './teacher-overview.css',
  providers: [MessageService]
})
export class TeacherOverview implements OnInit {

  courses: CourseDto[] = [];

  availabilityLoading = false;
  availabilitySaving = false;

  private readonly days: {
    label: string;
    value: TeacherAvailabilityDto.DayOfWeekEnum;
    formName: 'monday' | 'tuesday' | 'wednesday' | 'thursday' | 'friday';
  }[] = [
    {
      label: 'Monday',
      value: 'MONDAY',
      formName: 'monday'
    },
    {
      label: 'Tuesday',
      value: 'TUESDAY',
      formName: 'tuesday'
    },
    {
      label: 'Wednesday',
      value: 'WEDNESDAY',
      formName: 'wednesday'
    },
    {
      label: 'Thursday',
      value: 'THURSDAY',
      formName: 'thursday'
    },
    {
      label: 'Friday',
      value: 'FRIDAY',
      formName: 'friday'
    }
  ];

  private loadedAvailability: TeacherAvailabilityDto[] = [];

  availabilityForm!: ReturnType<FormBuilder['group']>;

  constructor(
    private courseService: CourseControllerService,
    private availabilityService: TeacherAvailabilityControllerService,
    private formBuilder: FormBuilder,
    private messageService: MessageService,
    private cdr: ChangeDetectorRef
  ) {  this.availabilityForm = this.formBuilder.group({
    monday: this.formBuilder.array([]),
    tuesday: this.formBuilder.array([]),
    wednesday: this.formBuilder.array([]),
    thursday: this.formBuilder.array([]),
    friday: this.formBuilder.array([])
  });}

  ngOnInit(): void {
    this.loadCourses();
  }

  loadCourses(): void {
    this.courseService.getAllCourses().subscribe({
      next: (courses) => {
        this.courses = courses;
        this.cdr.detectChanges();

        this.loadAvailability();
      },
      error: (error) => {
        console.error('Failed to load courses', error);
      }
    });
  }

  get totalCourses(): number {
    return this.courses.length;
  }

  get availabilityDays() {
    return this.days;
  }

  get mondayIntervals(): FormArray {
    return this.availabilityForm.get('monday') as FormArray;
  }

  get tuesdayIntervals(): FormArray {
    return this.availabilityForm.get('tuesday') as FormArray;
  }

  get wednesdayIntervals(): FormArray {
    return this.availabilityForm.get('wednesday') as FormArray;
  }

  get thursdayIntervals(): FormArray {
    return this.availabilityForm.get('thursday') as FormArray;
  }

  get fridayIntervals(): FormArray {
    return this.availabilityForm.get('friday') as FormArray;
  }

  getIntervals(
    day: 'monday' | 'tuesday' | 'wednesday' | 'thursday' | 'friday'
  ): FormArray {
    return this.availabilityForm.get(day) as FormArray;
  }

  addInterval(
    day: 'monday' | 'tuesday' | 'wednesday' | 'thursday' | 'friday'
  ): void {
    this.getIntervals(day).push(
      this.createInterval()
    );
  }

  removeInterval(
    day: 'monday' | 'tuesday' | 'wednesday' | 'thursday' | 'friday',
    index: number
  ): void {
    this.getIntervals(day).removeAt(index);
  }

  saveAvailability(): void {
    const teacherId = this.getTeacherId();

    if (!teacherId) {
      this.showError(
        'Could not determine the teacher for this overview.'
      );
      return;
    }

    if (this.availabilityForm.invalid) {
      this.availabilityForm.markAllAsTouched();
      return;
    }

    if (!this.validateIntervals()) {
      return;
    }

    this.availabilitySaving = true;

    const currentAvailability = this.getCurrentAvailability(teacherId);

    const currentIds = new Set(
      currentAvailability
        .map(item => item.id)
        .filter((id): id is string => !!id)
    );

    const deleteRequests: Observable<any>[] = this.loadedAvailability
      .filter(item => item.id && !currentIds.has(item.id))
      .map(item =>
        this.availabilityService.deleteTeacherAvailability(item.id!)
      );

    const saveRequests: Observable<TeacherAvailabilityDto>[] =
      currentAvailability.map(item => {
        if (item.id) {
          return this.availabilityService.updateTeacherAvailability(
            item.id,
            item
          );
        }

        return this.availabilityService.createTeacherAvailability(item);
      });

    const requests = [
      ...deleteRequests,
      ...saveRequests
    ];

    if (requests.length === 0) {
      this.availabilitySaving = false;
      return;
    }

    forkJoin(requests).subscribe({
      next: () => {
        this.availabilitySaving = false;
        this.showSuccess('Availability saved successfully.');
        this.loadAvailability();
      },
      error: (error) => {
        console.error('Failed to save availability', error);
        this.availabilitySaving = false;
        this.showError('Could not save availability.');
      }
    });
  }

  private loadAvailability(): void {
    const teacherId = this.getTeacherId();

    if (!teacherId) {
      this.availabilityLoading = false;
      return;
    }

    this.availabilityLoading = true;

    this.availabilityService
      .getTeacherAvailability(teacherId)
      .subscribe({
        next: (availability) => {
          this.loadedAvailability = availability;
          this.populateAvailabilityForm(availability);
          this.availabilityLoading = false;
          this.cdr.detectChanges();
        },
        error: (error) => {
          console.error(
            'Failed to load teacher availability',
            error
          );

          this.availabilityLoading = false;
          this.showError('Could not load teacher availability.');
        }
      });
  }

  private populateAvailabilityForm(
    availability: TeacherAvailabilityDto[]
  ): void {
    this.clearAvailabilityForm();

    for (const item of availability) {
      const day = this.getFormDay(item.dayOfWeek);

      if (!day) {
        continue;
      }

      this.getIntervals(day).push(
        this.createInterval(
          item.startTime,
          item.endTime,
          item.id
        )
      );
    }
  }

  private clearAvailabilityForm(): void {
    this.mondayIntervals.clear();
    this.tuesdayIntervals.clear();
    this.wednesdayIntervals.clear();
    this.thursdayIntervals.clear();
    this.fridayIntervals.clear();
  }

  private createInterval(
    startTime = '',
    endTime = '',
    id: string | null = null
  ) {
    return this.formBuilder.group({
      id: [id],
      startTime: [
        this.normalizeTime(startTime),
        Validators.required
      ],
      endTime: [
        this.normalizeTime(endTime),
        Validators.required
      ]
    });
  }

  private getCurrentAvailability(
    teacherId: string
  ): TeacherAvailabilityDto[] {
    const result: TeacherAvailabilityDto[] = [];

    for (const day of this.days) {
      const intervals = this.getIntervals(day.formName);

      for (const control of intervals.controls) {
        const value = control.value;

        result.push({
          id: value.id ?? undefined,
          teacherId,
          dayOfWeek: day.value,
          startTime: value.startTime,
          endTime: value.endTime
        });
      }
    }

    return result;
  }

  private validateIntervals(): boolean {
    for (const day of this.days) {
      const intervals = this.getIntervals(day.formName);

      const values = intervals.controls
        .map(control => control.value)
        .filter(value => value.startTime && value.endTime);

      for (const interval of values) {
        if (interval.startTime >= interval.endTime) {
          this.showError(
            `${day.label}: start time must be before end time.`
          );
          return false;
        }
      }

      const sorted = [...values].sort(
        (a, b) =>
          a.startTime.localeCompare(b.startTime)
      );

      for (let i = 1; i < sorted.length; i++) {
        if (sorted[i].startTime < sorted[i - 1].endTime) {
          this.showError(
            `${day.label}: availability intervals cannot overlap.`
          );
          return false;
        }
      }
    }

    return true;
  }

  private getTeacherId(): string | undefined {
    return this.courses.find(course => !!course.teacherId)?.teacherId;
  }

  private getFormDay(
    dayOfWeek?: TeacherAvailabilityDto.DayOfWeekEnum
  ): 'monday' | 'tuesday' | 'wednesday' | 'thursday' | 'friday' | undefined {
    switch (dayOfWeek) {
      case 'MONDAY':
        return 'monday';
      case 'TUESDAY':
        return 'tuesday';
      case 'WEDNESDAY':
        return 'wednesday';
      case 'THURSDAY':
        return 'thursday';
      case 'FRIDAY':
        return 'friday';
      default:
        return undefined;
    }
  }

  private normalizeTime(time?: string): string {
    if (!time) {
      return '';
    }

    return time.substring(0, 5);
  }

  private showSuccess(detail: string): void {
    this.messageService.add({
      severity: 'success',
      summary: 'Success',
      detail
    });
  }

  private showError(detail: string): void {
    this.messageService.add({
      severity: 'error',
      summary: 'Error',
      detail
    });
  }
}
