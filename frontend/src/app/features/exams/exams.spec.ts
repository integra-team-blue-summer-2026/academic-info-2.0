import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of, throwError } from 'rxjs';
import { vi } from 'vitest';

import { Exams } from './exams';
import { ExamControllerService } from '../../core/api/api/examController.service';
import { CourseControllerService } from '../../core/api/api/courseController.service';

describe('Exams', () => {
  let component: Exams;
  let fixture: ComponentFixture<Exams>;

  const exam = {
    id: 'e1',
    courseId: 'c1',
    teacherId: 't1',
    examType: 'PARTIAL',
    examFormat: 'WRITTEN',
    group: '221',
    room: 'C310',
    duration: 120,
    primaryDate: '2026-06-15T10:00:00',
    secondaryDate: '',
  };

  const courses = [
    { id: 'c1', courseName: 'Databases', teacherId: 't1' },
    { id: 'c2', courseName: 'Physics', teacherId: 't2' },
  ];

  const examService = {
    getAllExams: vi.fn(() => of([exam])),
    createExam: vi.fn(() => of(exam)),
    updateExam: vi.fn(() => of(exam)),
    deleteExam: vi.fn(() => of(undefined)),
  };

  const courseService = {
    getAll3: vi.fn(() => of(courses)),
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Exams],
      providers: [
        { provide: ExamControllerService, useValue: examService },
        { provide: CourseControllerService, useValue: courseService },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(Exams);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load the exams on init', () => {
    expect(component['exams']()).toEqual([exam]);
  });

  it('should load the courses on init', () => {
    expect(component['courses']()).toEqual(courses);
  });

  it('should show only the current teacher courses', () => {
    component['currentTeacherId'].set('t1');

    const filtered = component['teacherCourses']();

    expect(filtered.length).toBe(1);
    expect(filtered[0].courseName).toBe('Databases');
  });

  it('should return the course name for a known id', () => {
    expect(component['courseName']('c1')).toBe('Databases');
  });

  it('should fall back to the id for an unknown course', () => {
    expect(component['courseName']('missing')).toBe('missing');
  });

  it('should not save an invalid form', () => {
    component['openCreateDialog']();
    component['save']();

    expect(examService.createExam).not.toHaveBeenCalled();
  });

  it('should create an exam when the form is valid', () => {
    component['currentTeacherId'].set('t1');
    component['openCreateDialog']();
    component['form'].setValue({
      courseId: 'c1',
      examType: 'PARTIAL',
      examFormat: 'WRITTEN',
      group: '221',
      room: 'C310',
      duration: 120,
      primaryDate: '2026-06-15T10:00:00',
      secondaryDate: '',
    });

    component['save']();

    expect(examService.createExam).toHaveBeenCalled();
  });

  it('should show the backend error message when saving fails', () => {
    examService.createExam.mockReturnValueOnce(
      throwError(() => ({ error: { message: 'This group already has an exam on that day.' } })),
    );
    const errorSpy = vi.spyOn(component['messageService'], 'add');

    component['currentTeacherId'].set('t1');
    component['openCreateDialog']();
    component['form'].setValue({
      courseId: 'c1',
      examType: 'PARTIAL',
      examFormat: 'WRITTEN',
      group: '221',
      room: 'C310',
      duration: 120,
      primaryDate: '2026-06-15T10:00:00',
      secondaryDate: '',
    });

    component['save']();

    expect(errorSpy).toHaveBeenCalledWith(
      expect.objectContaining({ detail: 'This group already has an exam on that day.' }),
    );
  });
});
