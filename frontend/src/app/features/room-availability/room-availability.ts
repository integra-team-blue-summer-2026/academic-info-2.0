import { Component, computed, inject, OnInit, signal } from '@angular/core';
import {
  NonNullableFormBuilder,
  ReactiveFormsModule,
  Validators
} from '@angular/forms';
import { Button } from 'primeng/button';
import { ConfirmDialog } from 'primeng/confirmdialog';
import { Dialog } from 'primeng/dialog';
import { InputText } from 'primeng/inputtext';
import { Select } from 'primeng/select';
import { ConfirmationService, MessageService } from 'primeng/api';
import { Table, TableModule } from 'primeng/table';
import { Toast } from 'primeng/toast';

import { RoomControllerService } from '../../core/api/services/roomController.service';
import { RoomAvailabilityControllerService } from '../../core/api/services/roomAvailabilityController.service';
import { RoomDto } from '../../core/api/models/roomDto';
import { RoomAvailabilityDto } from '../../core/api/models/roomAvailabilityDto';

@Component({
  selector: 'app-room-availability',
  imports: [
    Button,
    ConfirmDialog,
    Dialog,
    InputText,
    ReactiveFormsModule,
    Select,
    TableModule,
    Toast
  ],
  templateUrl: './room-availability.html',
  styleUrl: './room-availability.css',
  providers: [ConfirmationService, MessageService],
})
export class RoomAvailability implements OnInit {

  private readonly roomService = inject(RoomControllerService);
  private readonly availabilityService = inject(RoomAvailabilityControllerService);
  private readonly confirmationService = inject(ConfirmationService);
  private readonly messageService = inject(MessageService);
  private readonly formBuilder = inject(NonNullableFormBuilder);

  protected readonly rooms = signal<RoomDto[]>([]);
  protected readonly availabilities = signal<RoomAvailabilityDto[]>([]);

  protected readonly loading = signal(false);
  protected readonly savingRoom = signal(false);
  protected readonly savingAvailability = signal(false);

  protected readonly roomDialogVisible = signal(false);
  protected readonly availabilityDialogVisible = signal(false);

  protected readonly editedRoom = signal<RoomDto | null>(null);
  protected readonly selectedRoom = signal<RoomDto | null>(null);

  protected readonly roomDialogHeader = computed(() =>
    this.editedRoom() ? 'Edit room' : 'Add room'
  );

  protected readonly availabilityDialogHeader = computed(() => {
    const room = this.selectedRoom();

    return room
      ? `Availability - ${room.roomName}`
      : 'Room availability';
  });

  protected readonly globalFilterFields = ['roomName', 'zone'];

  protected readonly days = [
    {
      label: 'Monday',
      value: RoomAvailabilityDto.DayOfWeekEnum.Monday
    },
    {
      label: 'Tuesday',
      value: RoomAvailabilityDto.DayOfWeekEnum.Tuesday
    },
    {
      label: 'Wednesday',
      value: RoomAvailabilityDto.DayOfWeekEnum.Wednesday
    },
    {
      label: 'Thursday',
      value: RoomAvailabilityDto.DayOfWeekEnum.Thursday
    },
    {
      label: 'Friday',
      value: RoomAvailabilityDto.DayOfWeekEnum.Friday
    }
  ];

  protected readonly roomForm = this.formBuilder.group({
    roomName: ['', Validators.required],
    zone: ['', Validators.required],
  });

  protected readonly availabilityForm = this.formBuilder.group({
    dayOfWeek: [
      RoomAvailabilityDto.DayOfWeekEnum.Monday,
      Validators.required
    ],
    startTime: ['', Validators.required],
    endTime: ['', Validators.required],
  });

  ngOnInit(): void {
    this.loadRooms();
    this.loadAvailabilities();
  }

  protected loadRooms(): void {
    this.loading.set(true);

    this.roomService.getAllRooms().subscribe({
      next: (rooms) => {
        this.rooms.set(rooms);
        this.loading.set(false);
      },
      error: () => {
        this.loading.set(false);
        this.showError('Could not load the rooms.');
      },
    });
  }

  protected loadAvailabilities(): void {
    this.availabilityService.getAllRoomAvailabilities().subscribe({
      next: (availabilities) => {
        this.availabilities.set(availabilities);
      },
      error: () => {
        this.showError('Could not load room availability.');
      },
    });
  }

  protected applyGlobalFilter(table: Table, event: Event): void {
    table.filterGlobal(
      (event.target as HTMLInputElement).value,
      'contains'
    );
  }

  protected getAvailabilityCount(room: RoomDto): number {
    if (!room.id) {
      return 0;
    }

    return this.availabilities()
      .filter(availability => availability.roomId === room.id)
      .length;
  }

  protected getRoomAvailabilities(): RoomAvailabilityDto[] {
    const roomId = this.selectedRoom()?.id;

    if (!roomId) {
      return [];
    }

    return this.availabilities()
      .filter(availability => availability.roomId === roomId);
  }

  protected openCreateRoomDialog(): void {
    this.editedRoom.set(null);
    this.roomForm.reset();
    this.roomDialogVisible.set(true);
  }

  protected openEditRoomDialog(room: RoomDto): void {
    this.editedRoom.set(room);

    this.roomForm.setValue({
      roomName: room.roomName ?? '',
      zone: room.zone ?? '',
    });

    this.roomDialogVisible.set(true);
  }

  protected closeRoomDialog(): void {
    this.roomDialogVisible.set(false);
    this.editedRoom.set(null);
    this.roomForm.reset();
  }

  protected onRoomDialogVisibleChange(visible: boolean): void {
    if (!visible) {
      this.closeRoomDialog();
    }
  }

  protected saveRoom(): void {
    if (this.roomForm.invalid) {
      this.roomForm.markAllAsTouched();
      return;
    }

    const formValue = this.roomForm.getRawValue();
    const edited = this.editedRoom();

    const normalizedRoomName =
      formValue.roomName.trim().toLowerCase();

    const roomAlreadyExists = this.rooms().some(room =>
      room.roomName?.trim().toLowerCase() === normalizedRoomName &&
      room.id !== edited?.id
    );

    if (roomAlreadyExists) {
      this.messageService.add({
        severity: 'error',
        summary: 'Room already exists',
        detail: 'A room with this name already exists.'
      });

      return;
    }

    this.savingRoom.set(true);

    const roomData: RoomDto = {
      roomName: formValue.roomName.trim(),
      zone: formValue.zone.trim()
    };

    const request$ = edited?.id
      ? this.roomService.updateRoom(
          edited.id,
          {
            ...edited,
            ...roomData
          }
        )
      : this.roomService.createRoom(roomData);

    request$.subscribe({
      next: () => {
        this.savingRoom.set(false);
        this.closeRoomDialog();
        this.loadRooms();

        this.messageService.add({
          severity: 'success',
          summary: edited ? 'Room updated' : 'Room added',
        });
      },
      error: () => {
        this.savingRoom.set(false);
        this.showError('Could not save the room.');
      },
    });
  }

  protected confirmDeleteRoom(room: RoomDto): void {
    if (!room.id) {
      return;
    }

    this.confirmationService.confirm({
      header: 'Delete room',
      message: `Are you sure you want to delete ${room.roomName}?`,
      icon: 'pi pi-exclamation-triangle',
      acceptButtonProps: {
        label: 'Delete',
        severity: 'danger'
      },
      rejectButtonProps: {
        label: 'Cancel',
        severity: 'secondary',
        outlined: true
      },
      accept: () => this.deleteRoom(room),
    });
  }

  protected openAvailabilityDialog(room: RoomDto): void {
    this.selectedRoom.set(room);

    this.availabilityForm.reset({
      dayOfWeek: RoomAvailabilityDto.DayOfWeekEnum.Monday,
      startTime: '',
      endTime: '',
    });

    this.availabilityDialogVisible.set(true);
  }

  protected closeAvailabilityDialog(): void {
    this.availabilityDialogVisible.set(false);
    this.selectedRoom.set(null);

    this.availabilityForm.reset({
      dayOfWeek: RoomAvailabilityDto.DayOfWeekEnum.Monday,
      startTime: '',
      endTime: '',
    });
  }

  protected onAvailabilityDialogVisibleChange(
    visible: boolean
  ): void {
    if (!visible) {
      this.closeAvailabilityDialog();
    }
  }

  protected saveAvailability(): void {
    if (this.availabilityForm.invalid) {
      this.availabilityForm.markAllAsTouched();
      return;
    }

    const room = this.selectedRoom();

    if (!room?.id) {
      return;
    }

    const formValue = this.availabilityForm.getRawValue();

    if (formValue.startTime >= formValue.endTime) {
      this.messageService.add({
        severity: 'error',
        summary: 'Invalid time interval',
        detail: 'End time must be later than start time.'
      });

      return;
    }

    const overlaps = this.availabilities()
      .filter(availability =>
        availability.roomId === room.id &&
        availability.dayOfWeek === formValue.dayOfWeek
      )
      .some(availability => {
        const existingStart =
          availability.startTime?.substring(0, 5) ?? '';

        const existingEnd =
          availability.endTime?.substring(0, 5) ?? '';

        return formValue.startTime < existingEnd &&
          formValue.endTime > existingStart;
      });

    if (overlaps) {
      this.messageService.add({
        severity: 'error',
        summary: 'Availability conflict',
        detail: 'This time interval overlaps with an existing availability.'
      });

      return;
    }

    this.savingAvailability.set(true);

    const availability: RoomAvailabilityDto = {
      roomId: room.id,
      dayOfWeek: formValue.dayOfWeek,
      startTime: formValue.startTime,
      endTime: formValue.endTime,
    };

    this.availabilityService
      .createRoomAvailability(availability)
      .subscribe({
        next: () => {
          this.savingAvailability.set(false);
          this.loadAvailabilities();

          this.availabilityForm.reset({
            dayOfWeek: RoomAvailabilityDto.DayOfWeekEnum.Monday,
            startTime: '',
            endTime: '',
          });

          this.messageService.add({
            severity: 'success',
            summary: 'Availability added',
          });
        },
        error: () => {
          this.savingAvailability.set(false);
          this.showError('Could not save room availability.');
        },
      });
  }

  protected confirmDeleteAvailability(
    availability: RoomAvailabilityDto
  ): void {
    if (!availability.id) {
      return;
    }

    this.confirmationService.confirm({
      header: 'Delete availability',
      message: 'Are you sure you want to delete this time interval?',
      icon: 'pi pi-exclamation-triangle',
      acceptButtonProps: {
        label: 'Delete',
        severity: 'danger'
      },
      rejectButtonProps: {
        label: 'Cancel',
        severity: 'secondary',
        outlined: true
      },
      accept: () => this.deleteAvailability(availability),
    });
  }

  protected formatDay(day?: string): string {
    if (!day) {
      return '';
    }

    return day.charAt(0) + day.slice(1).toLowerCase();
  }

  protected formatTime(time?: string): string {
    if (!time) {
      return '';
    }

    return time.substring(0, 5);
  }

  private deleteRoom(room: RoomDto): void {
    if (!room.id) {
      return;
    }

    this.roomService.deleteRoom(room.id).subscribe({
      next: () => {
        this.loadRooms();
        this.loadAvailabilities();

        this.messageService.add({
          severity: 'success',
          summary: 'Room deleted',
        });
      },
      error: () => {
        this.showError('Could not delete the room.');
      },
    });
  }

  private deleteAvailability(
    availability: RoomAvailabilityDto
  ): void {
    if (!availability.id) {
      return;
    }

    this.availabilityService
      .deleteRoomAvailability(availability.id)
      .subscribe({
        next: () => {
          this.loadAvailabilities();

          this.messageService.add({
            severity: 'success',
            summary: 'Availability deleted',
          });
        },
        error: () => {
          this.showError(
            'Could not delete room availability.'
          );
        },
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
