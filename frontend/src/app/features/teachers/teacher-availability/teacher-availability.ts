import {
  Component,
  EventEmitter,
  Input,
  OnChanges,
  Output,
  SimpleChanges,
  inject,
  signal,
} from '@angular/core';
import {
  FormArray,
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';

import { Button } from 'primeng/button';
import { MessageService } from 'primeng/api';
import { Toast } from 'primeng/toast';

import { TeacherDto } from '../../../core/api/models/teacherDto';
import { TeacherAvailabilityDto } from '../../../core/api/models/teacherAvailabilityDto';
import { TeacherAvailabilityControllerService } from '../../../core/api/services/teacherAvailabilityController.service';

type DayName =
  | 'MONDAY'
  | 'TUESDAY'
  | 'WEDNESDAY'
  | 'THURSDAY'
  | 'FRIDAY';

interface DayConfig {
  value: DayName;
  label: string;
}

@Component({
  selector: 'app-teacher-availability',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    Button,
    Toast,
  ],
  templateUrl: './teacher-availability.html',
  styleUrl: './teacher-availability.css',
  providers: [MessageService],
})
export class TeacherAvailability implements OnChanges {

  @Input({ required: true })
  teacher: TeacherDto | null = null;

  @Output()
  closed = new EventEmitter<void>();

  private readonly formBuilder = inject(FormBuilder);
  private readonly availabilityService = inject(
    TeacherAvailabilityControllerService
  );
  private readonly messageService = inject(MessageService);

  protected readonly loading = signal(false);
  protected readonly saving = signal(false);

  protected readonly days: DayConfig[] = [
    { value: 'MONDAY', label: 'Monday' },
    { value: 'TUESDAY', label: 'Tuesday' },
    { value: 'WEDNESDAY', label: 'Wednesday' },
    { value: 'THURSDAY', label: 'Thursday' },
    { value: 'FRIDAY', label: 'Friday' },
  ];

  protected readonly form = this.formBuilder.group({
    monday: this.createIntervals(),
    tuesday: this.createIntervals(),
    wednesday: this.createIntervals(),
    thursday: this.createIntervals(),
    friday: this.createIntervals(),
  });

  private readonly originalAvailability = signal<TeacherAvailabilityDto[]>([]);

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['teacher'] && this.teacher?.id) {
      this.loadAvailability();
    }
  }

  protected getIntervals(day: DayName): FormArray {
    return this.form.get(this.controlName(day)) as FormArray;
  }

  protected addInterval(day: DayName): void {
    this.getIntervals(day).push(this.createInterval());
  }

  protected removeInterval(day: DayName, index: number): void {
    this.getIntervals(day).removeAt(index);
  }

  protected save(): void {
    if (!this.teacher?.id) {
      return;
    }

    if (this.form.invalid) {
      this.form.markAllAsTouched();

      this.messageService.add({
        severity: 'warn',
        summary: 'Invalid availability',
        detail: 'Please enter valid start and end times.',
      });

      return;
    }

    if (!this.validateIntervals()) {
      return;
    }

    this.saving.set(true);

    const original = this.originalAvailability();

    const requests = this.buildChanges(original);

    this.executeRequests(requests);
  }

  protected close(): void {
    this.closed.emit();
  }

  private loadAvailability(): void {
    if (!this.teacher?.id) {
      return;
    }

    this.loading.set(true);

    this.availabilityService
      .getTeacherAvailability(this.teacher.id)
      .subscribe({
        next: (availability) => {
          this.originalAvailability.set(availability);
          this.populateForm(availability);
          this.loading.set(false);
        },
        error: () => {
          this.loading.set(false);

          this.messageService.add({
            severity: 'error',
            summary: 'Error',
            detail: 'Could not load teacher availability.',
          });
        },
      });
  }

  private populateForm(
    availability: TeacherAvailabilityDto[]
  ): void {
    this.clearForm();

    for (const item of availability) {
      const day = item.dayOfWeek as DayName;

      if (!this.isWeekday(day)) {
        continue;
      }

      this.getIntervals(day).push(
        this.createInterval(
          this.normalizeTime(item.startTime),
          this.normalizeTime(item.endTime)
        )
      );
    }
  }

  private clearForm(): void {
    for (const day of this.days) {
      const intervals = this.getIntervals(day.value);

      while (intervals.length > 0) {
        intervals.removeAt(0);
      }
    }
  }

  private createIntervals(): FormArray {
    return this.formBuilder.array([]);
  }

  private createInterval(
    startTime = '08:00',
    endTime = '17:00'
  ): FormGroup {
    return this.formBuilder.group({
      startTime: [
        startTime,
        [Validators.required],
      ],
      endTime: [
        endTime,
        [Validators.required],
      ],
    });
  }

  private validateIntervals(): boolean {
    for (const day of this.days) {
      const intervals = this.getIntervals(day.value);

      const values = intervals.getRawValue()
        .map((item: { startTime: string; endTime: string }) => ({
          startTime: item.startTime,
          endTime: item.endTime,
        }))
        .sort((a, b) =>
          a.startTime.localeCompare(b.startTime)
        );

      for (const interval of values) {
        if (interval.startTime >= interval.endTime) {
          this.messageService.add({
            severity: 'warn',
            summary: 'Invalid interval',
            detail: `${day.label}: start time must be before end time.`,
          });

          return false;
        }
      }

      for (let i = 1; i < values.length; i++) {
        const previous = values[i - 1];
        const current = values[i];

        if (current.startTime < previous.endTime) {
          this.messageService.add({
            severity: 'warn',
            summary: 'Overlapping intervals',
            detail: `${day.label}: intervals must not overlap.`,
          });

          return false;
        }
      }
    }

    return true;
  }

  private buildChanges(
    original: TeacherAvailabilityDto[]
  ): Array<{
    type: 'create' | 'update' | 'delete';
    item: TeacherAvailabilityDto;
  }> {
    if (!this.teacher?.id) {
      return [];
    }

    const current = this.getCurrentAvailability();

    const changes: Array<{
      type: 'create' | 'update' | 'delete';
      item: TeacherAvailabilityDto;
    }> = [];

    const originalById = new Map<string, TeacherAvailabilityDto>();

    for (const item of original) {
      if (item.id) {
        originalById.set(item.id, item);
      }
    }

    const currentIds = new Set<string>();

    for (const item of current) {
      if (item.id) {
        currentIds.add(item.id);

        changes.push({
          type: 'update',
          item,
        });
      } else {
        changes.push({
          type: 'create',
          item,
        });
      }
    }

    for (const item of original) {
      if (item.id && !currentIds.has(item.id)) {
        changes.push({
          type: 'delete',
          item,
        });
      }
    }

    return changes;
  }

  private getCurrentAvailability(): TeacherAvailabilityDto[] {
    if (!this.teacher?.id) {
      return [];
    }

    const result: TeacherAvailabilityDto[] = [];

    for (const day of this.days) {
      const intervals = this.getIntervals(day.value);

      for (const interval of intervals.getRawValue()) {
        result.push({
          teacherId: this.teacher.id,
          dayOfWeek: day.value,
          startTime: `${interval.startTime}:00`,
          endTime: `${interval.endTime}:00`,
        });
      }
    }

    /*
     * Păstrăm ID-urile vechi acolo unde putem.
     *
     * Asociem intervalele după zi + poziție.
     */
    const original = this.originalAvailability();

    for (const day of this.days) {
      const originalDay = original
        .filter(item => item.dayOfWeek === day.value)
        .sort((a, b) =>
          this.normalizeTime(a.startTime)
            .localeCompare(this.normalizeTime(b.startTime))
        );

      const currentDay = result.filter(
        item => item.dayOfWeek === day.value
      );

      for (
        let index = 0;
        index < currentDay.length && index < originalDay.length;
        index++
      ) {
        currentDay[index].id = originalDay[index].id;
      }
    }

    return result;
  }

  private executeRequests(
    requests: Array<{
      type: 'create' | 'update' | 'delete';
      item: TeacherAvailabilityDto;
    }>
  ): void {
    if (requests.length === 0) {
      this.saving.set(false);
      this.messageService.add({
        severity: 'success',
        summary: 'Availability saved',
      });
      return;
    }

    let remaining = requests.length;
    let failed = false;

    const completed = () => {
      remaining--;

      if (remaining > 0) {
        return;
      }

      this.saving.set(false);

      if (failed) {
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: 'Could not save all availability intervals.',
        });

        return;
      }

      this.messageService.add({
        severity: 'success',
        summary: 'Availability saved',
        detail: 'Teacher availability was updated successfully.',
      });

      this.loadAvailability();
    };

    for (const request of requests) {
      if (request.type === 'create') {
        this.createAvailability(request.item, failedRef => {
          failed = failed || failedRef;
          completed();
        });
      }

      if (request.type === 'update') {
        this.updateAvailability(request.item, failedRef => {
          failed = failed || failedRef;
          completed();
        });
      }

      if (request.type === 'delete') {
        this.deleteAvailability(request.item, failedRef => {
          failed = failed || failedRef;
          completed();
        });
      }
    }
  }

  private createAvailability(
    item: TeacherAvailabilityDto,
    callback: (failed: boolean) => void
  ): void {
    this.availabilityService
      .createTeacherAvailability(item)
      .subscribe({
        next: () => callback(false),
        error: () => callback(true),
      });
  }

  private updateAvailability(
    item: TeacherAvailabilityDto,
    callback: (failed: boolean) => void
  ): void {
    if (!item.id) {
      callback(true);
      return;
    }

    this.availabilityService
      .updateTeacherAvailability(item.id, item)
      .subscribe({
        next: () => callback(false),
        error: () => callback(true),
      });
  }

  private deleteAvailability(
    item: TeacherAvailabilityDto,
    callback: (failed: boolean) => void
  ): void {
    if (!item.id) {
      callback(false);
      return;
    }

    this.availabilityService
      .deleteTeacherAvailability(item.id)
      .subscribe({
        next: () => callback(false),
        error: () => callback(true),
      });
  }

  private controlName(day: DayName): string {
    const map: Record<DayName, string> = {
      MONDAY: 'monday',
      TUESDAY: 'tuesday',
      WEDNESDAY: 'wednesday',
      THURSDAY: 'thursday',
      FRIDAY: 'friday',
    };

    return map[day];
  }

  private isWeekday(day: string): day is DayName {
    return [
      'MONDAY',
      'TUESDAY',
      'WEDNESDAY',
      'THURSDAY',
      'FRIDAY',
    ].includes(day);
  }

  private normalizeTime(time: string | null | undefined): string {
    if (!time) {
      return '';
    }

    return time.substring(0, 5);
  }
}
