import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { beforeEach, describe, expect, it, vi } from 'vitest';

import { TeacherOverview } from './teacher-overview';
import { CourseControllerService } from '../../../core/api/services/courseController.service';
import { CourseDto } from '../../../core/api/models/courseDto';
import { TeacherAvailabilityControllerService } from '../../../core/api/services/teacherAvailabilityController.service';
import { TeacherAvailabilityDto } from '../../../core/api/models/teacherAvailabilityDto';

describe('TeacherOverview', () => {
  let component: TeacherOverview;
  let fixture: ComponentFixture<TeacherOverview>;

  const courses: CourseDto[] = [
    {
      id: 'course-1',
      teacherId: 'teacher-1',
      courseName: 'Software Engineering',
      syllabus: 'Software design and testing',
      credits: 6,
      description: 'Software engineering course'
    },
    {
      id: 'course-2',
      teacherId: 'teacher-1',
      courseName: 'Web Development',
      syllabus: 'HTML, CSS and Angular',
      credits: 5,
      description: 'Web development course'
    }
  ];

  const availability: TeacherAvailabilityDto[] = [
    {
      id: 'availability-1',
      teacherId: 'teacher-1',
      dayOfWeek: 'MONDAY',
      startTime: '08:00:00',
      endTime: '12:00:00'
    },
    {
      id: 'availability-2',
      teacherId: 'teacher-1',
      dayOfWeek: 'WEDNESDAY',
      startTime: '14:00:00',
      endTime: '17:00:00'
    }
  ];

  const courseServiceMock = {
    getAll4: vi.fn()
  };

  const availabilityServiceMock = {
    getTeacherAvailability: vi.fn(),
    createTeacherAvailability: vi.fn(),
    updateTeacherAvailability: vi.fn(),
    deleteTeacherAvailability: vi.fn()
  };

  beforeEach(async () => {
    courseServiceMock.getAll4.mockReset();
    courseServiceMock.getAll4.mockReturnValue(of(courses));

    availabilityServiceMock.getTeacherAvailability.mockReset();
    availabilityServiceMock.getTeacherAvailability.mockReturnValue(
      of(availability)
    );

    availabilityServiceMock.createTeacherAvailability.mockReset();
    availabilityServiceMock.createTeacherAvailability.mockImplementation(
      (item: TeacherAvailabilityDto) => of(item)
    );

    availabilityServiceMock.updateTeacherAvailability.mockReset();
    availabilityServiceMock.updateTeacherAvailability.mockImplementation(
      (id: string, item: TeacherAvailabilityDto) =>
        of({
          ...item,
          id
        })
    );

    availabilityServiceMock.deleteTeacherAvailability.mockReset();
    availabilityServiceMock.deleteTeacherAvailability.mockReturnValue(
      of(null)
    );

    await TestBed.configureTestingModule({
      imports: [TeacherOverview],
      providers: [
        {
          provide: CourseControllerService,
          useValue: courseServiceMock
        },
        {
          provide: TeacherAvailabilityControllerService,
          useValue: availabilityServiceMock
        }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(TeacherOverview);
    component = fixture.componentInstance;

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load courses on initialization', () => {
    expect(courseServiceMock.getAll4).toHaveBeenCalled();
    expect(component.courses.length).toBe(2);
  });

  it('should return total number of courses', () => {
    expect(component.totalCourses).toBe(2);
  });

  it('should contain the loaded course data', () => {
    expect(component.courses[0].courseName).toBe('Software Engineering');
    expect(component.courses[1].courseName).toBe('Web Development');
  });

  it('should load teacher availability after loading courses', () => {
    expect(
      availabilityServiceMock.getTeacherAvailability
    ).toHaveBeenCalledWith('teacher-1');
  });

  it('should populate the availability form', () => {
    expect(component.mondayIntervals.length).toBe(1);
    expect(component.wednesdayIntervals.length).toBe(1);

    expect(
      component.mondayIntervals.at(0).get('startTime')?.value
    ).toBe('08:00');

    expect(
      component.mondayIntervals.at(0).get('endTime')?.value
    ).toBe('12:00');
  });

  it('should add a new availability interval', () => {
    const initialLength = component.tuesdayIntervals.length;

    component.addInterval('tuesday');

    expect(component.tuesdayIntervals.length).toBe(
      initialLength + 1
    );
  });

  it('should remove an availability interval', () => {
    component.addInterval('friday');

    expect(component.fridayIntervals.length).toBe(1);

    component.removeInterval('friday', 0);

    expect(component.fridayIntervals.length).toBe(0);
  });

  it('should save existing availability', () => {
    component.saveAvailability();

    expect(
      availabilityServiceMock.updateTeacherAvailability
    ).toHaveBeenCalled();
  });

  it('should validate that availability intervals do not overlap', () => {
    component.mondayIntervals.clear();

    component.addInterval('monday');
    component.addInterval('monday');

    component.mondayIntervals
      .at(0)
      .patchValue({
        startTime: '08:00',
        endTime: '12:00'
      });

    component.mondayIntervals
      .at(1)
      .patchValue({
        startTime: '11:00',
        endTime: '14:00'
      });

    component.saveAvailability();

    expect(
      availabilityServiceMock.updateTeacherAvailability
    ).not.toHaveBeenCalled();

    expect(
      availabilityServiceMock.createTeacherAvailability
    ).not.toHaveBeenCalled();
  });
});
