import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { beforeEach, describe, expect, it, vi } from 'vitest';

import { TeacherOverview } from './teacher-overview';
import { CourseControllerService } from '../../../core/api/services/courseController.service';
import { CourseDto } from '../../../core/api/models/courseDto';

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

  const courseServiceMock = {
    getAll4: vi.fn()
  };

  beforeEach(async () => {
    courseServiceMock.getAll4.mockReset();
    courseServiceMock.getAll4.mockReturnValue(of(courses));

    await TestBed.configureTestingModule({
      imports: [TeacherOverview],
      providers: [
        {
          provide: CourseControllerService,
          useValue: courseServiceMock
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
});
