import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { of } from 'rxjs';
import { beforeEach, describe, expect, it, vi } from 'vitest';

import { CourseList } from './course-list';
import { CourseControllerService } from '../../../core/api/services/courseController.service';
import { CourseDto } from '../../../core/api/models/courseDto';

describe('CourseList', () => {
  let component: CourseList;
  let fixture: ComponentFixture<CourseList>;

  const courses: CourseDto[] = [
    {
      id: 'course-1',
      teacherId: 'teacher-1',
      courseName: 'Software Engineering',
      syllabus: 'Software design',
      credits: 6,
      description: 'Software engineering course'
    },
    {
      id: 'course-2',
      teacherId: 'teacher-2',
      courseName: 'Web Development',
      syllabus: 'HTML, CSS and Angular',
      credits: 5,
      description: 'Web development course'
    }
  ];

  const courseServiceMock = {
    getAll4: vi.fn(),
    delete4: vi.fn()
  };

  const routerMock = {
    navigate: vi.fn()
  };

  beforeEach(async () => {
    courseServiceMock.getAll4.mockReset();
    courseServiceMock.delete4.mockReset();
    routerMock.navigate.mockReset();

    courseServiceMock.getAll4.mockReturnValue(of(courses));
    courseServiceMock.delete4.mockReturnValue(of(undefined));

    await TestBed.configureTestingModule({
      imports: [CourseList],
      providers: [
        {
          provide: CourseControllerService,
          useValue: courseServiceMock
        },
        {
          provide: Router,
          useValue: routerMock
        }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(CourseList);
    component = fixture.componentInstance;

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load courses on initialization', () => {
    expect(courseServiceMock.getAll4).toHaveBeenCalled();
    expect(component.courses.length).toBe(2);
    expect(component.filteredCourses.length).toBe(2);
  });

  it('should filter courses by course name', () => {
    component.searchTerm = 'software';

    component.filterCourses();

    expect(component.filteredCourses.length).toBe(1);
    expect(component.filteredCourses[0].courseName)
      .toBe('Software Engineering');
  });

  it('should filter courses by teacher id', () => {
    component.teacherSearchTerm = 'teacher-2';

    component.filterCourses();

    expect(component.filteredCourses.length).toBe(1);
    expect(component.filteredCourses[0].courseName)
      .toBe('Web Development');
  });

  it('should navigate to course details', () => {
    component.viewCourseDetails(courses[0]);

    expect(routerMock.navigate)
      .toHaveBeenCalledWith(['/courses', 'course-1']);
  });

  it('should not navigate when course has no id', () => {
    const courseWithoutId: CourseDto = {
      teacherId: 'teacher-1',
      courseName: 'Test Course',
      syllabus: 'Test',
      credits: 5,
      description: 'Test'
    };

    component.viewCourseDetails(courseWithoutId);

    expect(routerMock.navigate).not.toHaveBeenCalled();
  });
});
