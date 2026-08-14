import { ComponentFixture, TestBed } from '@angular/core/testing';
import {
  HttpClientTestingModule,
  HttpTestingController
} from '@angular/common/http/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { beforeEach, afterEach, describe, expect, it, vi } from 'vitest';

import { CourseDetails } from './course-details';

describe('CourseDetails', () => {
  let component: CourseDetails;
  let fixture: ComponentFixture<CourseDetails>;
  let httpMock: HttpTestingController;

  const routerMock = {
    navigate: vi.fn()
  };

  const courseId = '2a9f328a-4ee2-4752-897f-d7355bd14395';

  beforeEach(async () => {
    routerMock.navigate.mockClear();

    await TestBed.configureTestingModule({
      imports: [
        CourseDetails,
        HttpClientTestingModule
      ],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: {
                get: () => courseId
              }
            }
          }
        },
        {
          provide: Router,
          useValue: routerMock
        }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(CourseDetails);
    component = fixture.componentInstance;
    httpMock = TestBed.inject(HttpTestingController);

    fixture.detectChanges();
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should create', () => {
    expect(component).toBeTruthy();

    httpMock.expectOne(
      `http://localhost:8080/api/courses/${courseId}`
    ).flush({
      id: courseId,
      teacherId: 'teacher-id',
      courseName: 'Software Engineering',
      syllabus: 'Software design',
      credits: 6,
      description: 'Course description'
    });

    httpMock.expectOne(
      'http://localhost:8080/api/exams'
    ).flush([]);
  });

  it('should load course details', () => {
    httpMock.expectOne(
      `http://localhost:8080/api/courses/${courseId}`
    ).flush({
      id: courseId,
      teacherId: 'teacher-id',
      courseName: 'Software Engineering',
      syllabus: 'Software design',
      credits: 6,
      description: 'Course description'
    });

    httpMock.expectOne(
      'http://localhost:8080/api/exams'
    ).flush([]);

    expect(component.course?.courseName).toBe('Software Engineering');
    expect(component.course?.credits).toBe(6);
  });

  it('should filter exams by course id', () => {
    httpMock.expectOne(
      `http://localhost:8080/api/courses/${courseId}`
    ).flush({
      id: courseId,
      teacherId: 'teacher-id',
      courseName: 'Software Engineering',
      syllabus: 'Software design',
      credits: 6,
      description: 'Course description'
    });

    httpMock.expectOne(
      'http://localhost:8080/api/exams'
    ).flush([
      {
        id: 'exam-1',
        courseId: courseId,
        examType: 'Written Exam',
        examDate: '2026-09-10',
        room: 'A101'
      },
      {
        id: 'exam-2',
        courseId: 'different-course',
        examType: 'Oral Exam',
        examDate: '2026-09-12',
        room: 'B202'
      }
    ]);

    expect(component.exams.length).toBe(1);
    expect(component.exams[0].examType).toBe('Written Exam');
  });

  it('should navigate back to courses', () => {
    httpMock.expectOne(
      `http://localhost:8080/api/courses/${courseId}`
    ).flush({
      id: courseId,
      teacherId: 'teacher-id',
      courseName: 'Software Engineering',
      syllabus: 'Software design',
      credits: 6,
      description: 'Course description'
    });

    httpMock.expectOne(
      'http://localhost:8080/api/exams'
    ).flush([]);

    component.goBack();

    expect(routerMock.navigate).toHaveBeenCalledWith(['/courses']);
  });
});
