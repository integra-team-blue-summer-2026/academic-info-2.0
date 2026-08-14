import {Component, computed, inject, OnInit, signal} from '@angular/core';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Button } from 'primeng/button';
import { ConfirmDialog } from 'primeng/confirmdialog';
import { Dialog } from 'primeng/dialog';
import { InputText } from 'primeng/inputtext';
import { ConfirmationService, MessageService } from 'primeng/api';
import { Table, TableModule } from 'primeng/table';
import { Toast } from 'primeng/toast';

import { TeacherControllerService } from '../../core/api/services/teacherController.service';
import { TeacherDto } from '../../core/api/models/teacherDto';


@Component({
  selector: 'app-teachers',
  imports: [Button, ConfirmDialog, Dialog, InputText, ReactiveFormsModule, TableModule, Toast],
  templateUrl: './teachers.html',
  styleUrl: './teachers.css',
  providers: [ConfirmationService, MessageService],
})
export class Teachers implements OnInit {
  private readonly teacherService = inject(TeacherControllerService);
  private readonly confirmationService = inject(ConfirmationService);
  private readonly messageService = inject(MessageService);
  private readonly formBuilder = inject(NonNullableFormBuilder);

  protected readonly teachers = signal<TeacherDto[]>([]);
  protected readonly loading = signal(false);
  protected readonly saving = signal(false);
  protected readonly formDialogVisible = signal(false);
  protected readonly passwordDialogVisible = signal(false);
  protected readonly generatedPassword = signal('');
  protected readonly editedTeacher = signal<TeacherDto | null>(null);

  protected readonly formDialogHeader = computed(() =>
    this.editedTeacher() ? 'Edit teacher' : 'Add teacher'
  );

  protected readonly globalFilterFields = ['firstName', 'lastName', 'title', 'department'];

  protected readonly form = this.formBuilder.group({
    firstName: ['', Validators.required],
    lastName: ['', Validators.required],
    title: ['', Validators.required],
    department: ['', Validators.required],
  });

  ngOnInit(): void {
    this.loadTeachers();
  }

  protected loadTeachers(): void {
    this.loading.set(true);

    this.teacherService
      .getAll(undefined, undefined, { httpHeaderAccept: 'application/json' as '*/*' })
      .subscribe({
      next: (teachers) => {
        this.teachers.set(teachers);
        this.loading.set(false);
      },
      error: () => {
        this.loading.set(false);
        this.showError('Could not load the teachers.');
      },
    });
  }

  protected applyGlobalFilter(table: Table, event: Event): void {
    table.filterGlobal((event.target as HTMLInputElement).value, 'contains');
  }

  protected openCreateDialog(): void {
    this.editedTeacher.set(null);
    this.form.reset();
    this.formDialogVisible.set(true);
  }

  protected openEditDialog(teacher: TeacherDto): void {
    this.editedTeacher.set(teacher);
    this.form.setValue({
      firstName: teacher.firstName ?? '',
      lastName: teacher.lastName ?? '',
      title: teacher.title ?? '',
      department: teacher.department ?? '',
    });
    this.formDialogVisible.set(true);
  }

  protected onFormDialogVisibleChange(visible: boolean): void {
    if (!visible) {
      this.closeFormDialog();
    }
  }

  protected onPasswordDialogVisibleChange(visible: boolean): void {
    if (!visible) {
      this.closePasswordDialog();
    }
  }

  protected closeFormDialog(): void {
    this.formDialogVisible.set(false);
    this.editedTeacher.set(null);
    this.form.reset();
  }

  protected save(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const formValue = this.form.getRawValue();
    const edited = this.editedTeacher();

    this.saving.set(true);

    const request$ = edited?.id
      ? this.teacherService.update(edited.id, { ...edited, ...formValue })
      : this.teacherService.create(formValue);

    request$.subscribe({
      next: () => {
        this.saving.set(false);
        this.closeFormDialog();
        this.loadTeachers();
        this.messageService.add({
          severity: 'success',
          summary: edited ? 'Teacher updated' : 'Teacher added',
        });
      },
      error: () => {
        this.saving.set(false);
        this.showError('Could not save the teacher.');
      },
    });
  }

  protected confirmDelete(teacher: TeacherDto): void {
    if (!teacher.id) {
      return;
    }

    this.confirmationService.confirm({
      header: 'Delete teacher',
      message: `Are you sure you want to delete ${teacher.firstName} ${teacher.lastName}?`,
      icon: 'pi pi-exclamation-triangle',
      acceptButtonProps: { label: 'Delete', severity: 'danger' },
      rejectButtonProps: { label: 'Cancel', severity: 'secondary', outlined: true },
      accept: () => this.deleteTeacher(teacher),
    });
  }

  protected regeneratePassword(teacher: TeacherDto): void {
    const id = teacher.id;

    if (!id) {
      return;
    }

    this.confirmationService.confirm({
      header: 'Regenerate password',
      message: 'The current password will stop working. The new one is shown only once.',
      icon: 'pi pi-key',
      acceptButtonProps: { label: 'Regenerate' },
      rejectButtonProps: { label: 'Cancel', severity: 'secondary', outlined: true },
      accept: () => {
        this.teacherService
          .regeneratePassword(id, undefined, undefined, { httpHeaderAccept: 'application/json' as '*/*' })
          .subscribe({
          next: (generated) => {
            this.generatedPassword.set(generated.password ?? '');
            this.passwordDialogVisible.set(true);
          },
          error: () => this.showError('Could not regenerate the password.'),
        });
      },
    });
  }

  protected closePasswordDialog(): void {
    this.passwordDialogVisible.set(false);
    this.generatedPassword.set('');
  }

  protected copyPassword(): void {
    const password = this.generatedPassword();

    if (!password || !navigator.clipboard) {
      return;
    }

    navigator.clipboard.writeText(password).then(() =>
      this.messageService.add({ severity: 'info', summary: 'Password copied' })
    );
  }

  private deleteTeacher(teacher: TeacherDto): void {
    if (!teacher.id) {
      return;
    }

    this.teacherService._delete(teacher.id).subscribe({
      next: () => {
        this.loadTeachers();
        this.messageService.add({ severity: 'success', summary: 'Teacher deleted' });
      },
      error: () => this.showError('Could not delete the teacher.'),
    });
  }

  private showError(detail: string): void {
    this.messageService.add({ severity: 'error', summary: 'Error', detail });
  }
}
