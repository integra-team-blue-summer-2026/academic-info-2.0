import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ConfirmationService } from 'primeng/api';
import { of } from 'rxjs';
import { vi } from 'vitest';

import { Teachers } from './teachers';
import { TeacherControllerService } from '../../core/api/services/teacherController.service';

describe('Teachers', () => {
  let component: Teachers;
  let fixture: ComponentFixture<Teachers>;

  const teacher = {
    id: '1',
    firstName: 'Ana',
    lastName: 'Popescu',
    title: 'Prof.',
    department: 'Computer Science',
  };

  const teacherService = {
    getAll: vi.fn(() => of([teacher])),
    create: vi.fn(() => of(teacher)),
    update: vi.fn(() => of(teacher)),
    _delete: vi.fn(() => of(undefined)),
    regeneratePassword: vi.fn(() => of({ password: 'NewPass123!' })),
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Teachers],
      providers: [{ provide: TeacherControllerService, useValue: teacherService }],
    }).compileComponents();

    fixture = TestBed.createComponent(Teachers);
    component = fixture.componentInstance;

    const confirmation = fixture.debugElement.injector.get(ConfirmationService);
    vi.spyOn(confirmation, 'confirm').mockImplementation((options) => {
      options.accept?.();
      return confirmation;
    });

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load the teachers on init', () => {
    expect(component['teachers']()).toEqual([teacher]);
  });

  it('should fill the form when editing a teacher', () => {
    component['openEditDialog'](teacher);

    expect(component['form'].getRawValue().lastName).toBe('Popescu');
  });

  it('should not save an invalid form', () => {
    component['openCreateDialog']();
    component['save']();

    expect(teacherService.create).not.toHaveBeenCalled();
  });

  it('should create a teacher when the form is valid', () => {
    component['openCreateDialog']();
    component['form'].setValue({
      firstName: 'Mihai',
      lastName: 'Ionescu',
      title: 'Lect.',
      department: 'Mathematics',
    });

    component['save']();

    expect(teacherService.create).toHaveBeenCalled();
  });

  it('should show the generated password', () => {
    component['regeneratePassword'](teacher);

    expect(component['generatedPassword']()).toBe('NewPass123!');
    expect(component['passwordDialogVisible']()).toBe(true);
  });

  it('should delete a teacher after confirmation', () => {
    component['confirmDelete'](teacher);

    expect(teacherService._delete).toHaveBeenCalledWith('1');
  });
});
