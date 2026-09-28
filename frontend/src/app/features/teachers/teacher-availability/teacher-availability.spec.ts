import {
  ComponentFixture,
  TestBed,
} from '@angular/core/testing';

import {
  beforeEach,
  describe,
  expect,
  it,
  vi,
} from 'vitest';

import { TeacherAvailability } from './teacher-availability';

import { TeacherAvailabilityControllerService } from '../../../core/api/services/teacherAvailabilityController.service';

import { TeacherDto } from '../../../core/api/models/teacherDto';


describe('TeacherAvailability', () => {

  let fixture: ComponentFixture<TeacherAvailability>;
  let component: TeacherAvailability;

  let availabilityServiceMock: {
    getTeacherAvailability: ReturnType<typeof vi.fn>;
    createTeacherAvailability: ReturnType<typeof vi.fn>;
    updateTeacherAvailability: ReturnType<typeof vi.fn>;
    deleteTeacherAvailability: ReturnType<typeof vi.fn>;
  };

  const teacher: TeacherDto = {
    id: 'teacher-1',
    firstName: 'John',
    lastName: 'Doe',
    title: 'Professor',
    department: 'Computer Science',
  };


  beforeEach(async () => {

    availabilityServiceMock = {
      getTeacherAvailability: vi.fn(),
      createTeacherAvailability: vi.fn(),
      updateTeacherAvailability: vi.fn(),
      deleteTeacherAvailability: vi.fn(),
    };

    availabilityServiceMock.getTeacherAvailability.mockReturnValue({
      subscribe: (observer: any) => {
        observer.next([]);
        observer.complete?.();
      },
    });

    availabilityServiceMock.createTeacherAvailability.mockReturnValue({
      subscribe: (observer: any) => {
        observer.next({});
        observer.complete?.();
      },
    });

    availabilityServiceMock.updateTeacherAvailability.mockReturnValue({
      subscribe: (observer: any) => {
        observer.next({});
        observer.complete?.();
      },
    });

    availabilityServiceMock.deleteTeacherAvailability.mockReturnValue({
      subscribe: (observer: any) => {
        observer.next({});
        observer.complete?.();
      },
    });


    await TestBed.configureTestingModule({
      imports: [
        TeacherAvailability,
      ],
      providers: [
        {
          provide: TeacherAvailabilityControllerService,
          useValue: availabilityServiceMock,
        },
      ],
    }).compileComponents();


    fixture = TestBed.createComponent(
      TeacherAvailability
    );

    component = fixture.componentInstance;

    fixture.componentRef.setInput('teacher', teacher);

    fixture.detectChanges();
  });


  it('should create', () => {
    expect(component).toBeTruthy();
  });


  it('should load teacher availability', () => {
    expect(
      availabilityServiceMock.getTeacherAvailability
    ).toHaveBeenCalledWith('teacher-1');
  });


  it('should create an interval', () => {
    const initialLength =
      component['getIntervals']('MONDAY').length;

    component['addInterval']('MONDAY');

    expect(
      component['getIntervals']('MONDAY').length
    ).toBe(initialLength + 1);
  });


  it('should remove an interval', () => {
    component['addInterval']('MONDAY');

    expect(
      component['getIntervals']('MONDAY').length
    ).toBe(1);

    component['removeInterval'](
      'MONDAY',
      0
    );

    expect(
      component['getIntervals']('MONDAY').length
    ).toBe(0);
  });


  it('should reject an interval where start is after end', () => {
    component['addInterval']('MONDAY');

    const interval =
      component['getIntervals']('MONDAY')
        .at(0);

    interval.patchValue({
      startTime: '14:00',
      endTime: '10:00',
    });

    const result =
      component['validateIntervals']();

    expect(result).toBe(false);
  });


  it('should reject overlapping intervals', () => {
    component['addInterval']('MONDAY');
    component['addInterval']('MONDAY');

    const intervals =
      component['getIntervals']('MONDAY');

    intervals.at(0).patchValue({
      startTime: '08:00',
      endTime: '12:00',
    });

    intervals.at(1).patchValue({
      startTime: '11:00',
      endTime: '14:00',
    });

    const result =
      component['validateIntervals']();

    expect(result).toBe(false);
  });


  it('should accept non-overlapping intervals', () => {
    component['addInterval']('MONDAY');
    component['addInterval']('MONDAY');

    const intervals =
      component['getIntervals']('MONDAY');

    intervals.at(0).patchValue({
      startTime: '08:00',
      endTime: '12:00',
    });

    intervals.at(1).patchValue({
      startTime: '14:00',
      endTime: '17:00',
    });

    const result =
      component['validateIntervals']();

    expect(result).toBe(true);
  });


  it('should emit closed event', () => {
    const emitSpy = vi.spyOn(
      component.closed,
      'emit'
    );

    (component as any).close();

    expect(
      emitSpy
    ).toHaveBeenCalled();
  });

});
