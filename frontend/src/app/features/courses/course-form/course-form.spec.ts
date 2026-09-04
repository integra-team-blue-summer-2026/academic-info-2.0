import { ComponentFixture, TestBed } from '@angular/core/testing';
import { SimpleChange } from '@angular/core';
import { of } from 'rxjs';
import { beforeEach, describe, expect, it, vi } from 'vitest';

import { CourseForm } from './course-form';
import { CourseControllerService } from '../../../core/api/services/courseController.service';
import { CourseDto } from '../../../core/api/models/courseDto';

describe('CourseForm', () => {
  let component: CourseForm;
  let fixture: ComponentFixture<CourseForm>;

  const courseServiceMock = {
    create4: vi.fn(),
    update4: vi.fn()
  };

  beforeEach(async () => {
    courseServiceMock.create4.mockReset();
    courseServiceMock.update4.mockReset();

    courseServiceMock.create4.mockReturnValue(of({} as CourseDto));
    courseServiceMock.update4.mockReturnValue(of({} as CourseDto));

    await TestBed.configureTestingModule({
      imports: [CourseForm],
      providers: [
        {
          provide: CourseControllerService,
          useValue: courseServiceMock
        }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(CourseForm);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should populate form when course is provided', () => {
    component.course = {
      id: 'course-1',
      teacherId: 'teacher-1',
      courseName: 'Software Engineering',
      syllabus: 'Software design',
      credits: 6,
      description: 'Course description'
    };

    component.ngOnChanges({
      course: new SimpleChange(null, component.course, false)
    });

    expect(component.form.value.courseName).toBe('Software Engineering');
    expect(component.form.value.teacherId).toBe('teacher-1');
    expect(component.form.value.credits).toBe(6);
  });

  it('should create a new course', () => {
    const savedSpy = vi.spyOn(component.saved, 'emit');

    component.form.setValue({
      courseName: 'Web Development',
      teacherId: 'teacher-1',
      syllabus: 'HTML, CSS and Angular',
      credits: 5,
      description: 'Web development course'
    });

    component.submit();

    expect(courseServiceMock.create4).toHaveBeenCalledWith({
      courseName: 'Web Development',
      teacherId: 'teacher-1',
      syllabus: 'HTML, CSS and Angular',
      credits: 5,
      description: 'Web development course'
    });

    expect(savedSpy).toHaveBeenCalled();
  });

  it('should update an existing course', () => {
    const savedSpy = vi.spyOn(component.saved, 'emit');

    component.course = {
      id: 'course-1',
      teacherId: 'teacher-1',
      courseName: 'Old Course',
      syllabus: 'Old syllabus',
      credits: 4,
      description: 'Old description'
    };

    component.form.setValue({
      courseName: 'Updated Course',
      teacherId: 'teacher-1',
      syllabus: 'Updated syllabus',
      credits: 6,
      description: 'Updated description'
    });

    component.submit();

    expect(courseServiceMock.update4).toHaveBeenCalledWith(
      'course-1',
      {
        courseName: 'Updated Course',
        teacherId: 'teacher-1',
        syllabus: 'Updated syllabus',
        credits: 6,
        description: 'Updated description'
      }
    );

    expect(savedSpy).toHaveBeenCalled();
  });

  it('should not submit an invalid form', () => {
    component.form.reset();

    component.submit();

    expect(courseServiceMock.create4).not.toHaveBeenCalled();
    expect(courseServiceMock.update4).not.toHaveBeenCalled();
  });

  it('should emit cancelled when cancel is called', () => {
    const cancelledSpy = vi.spyOn(component.cancelled, 'emit');

    component.cancel();

    expect(cancelledSpy).toHaveBeenCalled();
  });
});
