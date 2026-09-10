import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { of } from 'rxjs';
import { describe, it, beforeEach, expect, vi } from 'vitest';

import { CourseDetailsTeacher } from './course-details-teacher';

import { CourseControllerService } from '../../../core/api/api/courseController.service';
import { StudentControllerService } from '../../../core/api/api/studentController.service';
import { StudentCourseControllerService } from '../../../core/api/api/studentCourseController.service';
import { ExamControllerService } from '../../../core/api/api/examController.service';
import {StudentCourseDto} from '../../../core/api';

describe('CourseDetailsTeacher', () => {
  let component: CourseDetailsTeacher;
  let fixture: ComponentFixture<CourseDetailsTeacher>;

  const courseId = '2a9f328a-4ee2-4752-897f-d7355bd14395';

  const course = {
    id: courseId,
    teacherId: 'teacher-id',
    courseName: 'Software Engineering',
    syllabus: 'Software design',
    credits: 6,
    description: 'Course description',
  };

  const student = {
    id: 'student-1',
    firstName: 'Ion',
    lastName: 'Popescu',
    email: 'ion.popescu@example.com',
    nationalId: '1960101123456',
    dateOfBirth: '1996-01-01',
  };

  const secondStudent = {
    id: 'student-2',
    firstName: 'Maria',
    lastName: 'Ionescu',
    email: 'maria.ionescu@example.com',
    nationalId: '2960202123456',
    dateOfBirth: '1996-02-02',
  };

  const enrollment = {
    courseId,
    studentId: 'student-1',
  };

  const exam = {
    id: 'exam-1',
    courseId,
    examType: 'Written Exam',
    examDate: '2026-09-10',
    room: 'A101',
  };

  const differentCourseExam = {
    id: 'exam-2',
    courseId: 'different-course',
    examType: 'Oral Exam',
    examDate: '2026-09-12',
    room: 'B202',
  };

  const courseService = {
    getById4: vi.fn(() => of(course)),
  };

  const studentService = {
    getAll1: vi.fn(() => of([student, secondStudent])),
    getById1: vi.fn(() => of(student)),
  };

  const studentCourseService = {
    getByCourseId: vi.fn(() => of<StudentCourseDto[]>([enrollment])),
    create3: vi.fn(() => of(enrollment)),
  };

  const examService = {
    getAll4: vi.fn(() => of([exam, differentCourseExam])),
    create4: vi.fn(() => of(exam)),
  };

  const routerMock = {
    navigate: vi.fn(),
  };

  beforeEach(async () => {
    vi.clearAllMocks();

    // Default values for ngOnInit()
    courseService.getById4.mockReturnValue(of(course));
    studentCourseService.getByCourseId.mockReturnValue(of([]));
    examService.getAll4.mockReturnValue(
      of([exam, differentCourseExam])
    );
    studentService.getById1.mockReturnValue(of(student));
    studentService.getAll1.mockReturnValue(
      of([student, secondStudent])
    );

    await TestBed.configureTestingModule({
      imports: [CourseDetailsTeacher],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: {
                get: () => courseId,
              },
            },
          },
        },
        {
          provide: Router,
          useValue: routerMock,
        },
        {
          provide: CourseControllerService,
          useValue: courseService,
        },
        {
          provide: StudentControllerService,
          useValue: studentService,
        },
        {
          provide: StudentCourseControllerService,
          useValue: studentCourseService,
        },
        {
          provide: ExamControllerService,
          useValue: examService,
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(CourseDetailsTeacher);
    component = fixture.componentInstance;

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load course details', () => {
    expect(courseService.getById4).toHaveBeenCalledWith(courseId);

    expect(component.course).toEqual(course);
    expect(component.course?.courseName).toBe(
      'Software Engineering'
    );
    expect(component.course?.credits).toBe(6);
    expect(component.course?.teacherId).toBe('teacher-id');
  });

  it('should load course students', () => {
    studentCourseService.getByCourseId.mockReturnValue(
      of([enrollment])
    );

    // Reload the component so ngOnInit uses the new mock value.
    fixture = TestBed.createComponent(CourseDetailsTeacher);
    component = fixture.componentInstance;
    fixture.detectChanges();

    expect(studentCourseService.getByCourseId)
      .toHaveBeenCalledWith(courseId);

    expect(studentService.getById1)
      .toHaveBeenCalledWith('student-1');

    expect(component.students.length).toBe(1);
    expect(component.students[0].student).toEqual(student);
    expect(component.students[0].enrollment).toEqual(enrollment);
  });

  it('should load only exams belonging to the current course', () => {
    expect(examService.getAll4).toHaveBeenCalled();

    expect(component.exams.length).toBe(1);
    expect(component.exams[0]).toEqual(exam);
    expect(component.exams[0].courseId).toBe(courseId);
  });

  it('should open the add student dialog and load available students', () => {
    component.openAddStudentDialog();

    expect(component.addStudentDialogVisible).toBe(true);
    expect(component.loadingAvailableStudents).toBe(false);

    expect(studentService.getAll1).toHaveBeenCalled();

    expect(component.availableStudents).toEqual([
      student,
      secondStudent,
    ]);
  });

  it('should exclude already enrolled students from available students', () => {
    studentCourseService.getByCourseId.mockReturnValue(
      of([enrollment])
    );

    fixture = TestBed.createComponent(CourseDetailsTeacher);
    component = fixture.componentInstance;
    fixture.detectChanges();

    component.openAddStudentDialog();

    expect(component.availableStudents.length).toBe(2);

    expect(component.availableStudentsForCourse.length).toBe(1);
    expect(component.availableStudentsForCourse[0]).toEqual(
      secondStudent
    );
  });

  it('should add a student to the course', () => {
    // Course is already loaded by ngOnInit().
    component.openAddStudentDialog();

    component.selectedStudentId = 'student-2';

    component.addSelectedStudent();

    expect(studentCourseService.create3).toHaveBeenCalledWith({
      courseId,
      studentId: 'student-2',
    });

    expect(component.addStudentDialogVisible).toBe(false);
    expect(component.selectedStudentId).toBe('');
    expect(component.submittingStudent).toBe(false);

    expect(component.successMessage).toBe(
      'Student was added to the course.'
    );
  });

  it('should not add a student when no student is selected', () => {
    component.openAddStudentDialog();

    component.selectedStudentId = '';

    component.addSelectedStudent();

    expect(studentCourseService.create3).not.toHaveBeenCalled();

    expect(component.errorMessage).toBe(
      'Please select a student.'
    );

    expect(component.submittingStudent).toBe(false);
  });

  it('should not add an already enrolled student', () => {
    studentCourseService.getByCourseId.mockReturnValue(
      of([enrollment])
    );

    fixture = TestBed.createComponent(CourseDetailsTeacher);
    component = fixture.componentInstance;
    fixture.detectChanges();

    component.selectedStudentId = 'student-1';

    component.addSelectedStudent();

    expect(studentCourseService.create3).not.toHaveBeenCalled();

    expect(component.errorMessage).toBe(
      'This student is already enrolled in the course.'
    );
  });

  it('should open the create exam dialog', () => {
    component.openCreateExamDialog();

    expect(component.createExamDialogVisible).toBe(true);
    expect(component.examType).toBe('');
    expect(component.examDate).toBe('');
    expect(component.examRoom).toBe('');
  });

  it('should create an exam', () => {
    component.openCreateExamDialog();

    component.examType = 'Final';
    component.examDate = '2026-09-10';
    component.examRoom = 'A101';

    component.createExam();

    expect(examService.create4).toHaveBeenCalledWith({
      courseId,
      examType: 'Final',
      examDate: '2026-09-10',
      room: 'A101',
    });

    expect(component.createExamDialogVisible).toBe(false);
    expect(component.submittingExam).toBe(false);

    expect(component.examType).toBe('');
    expect(component.examDate).toBe('');
    expect(component.examRoom).toBe('');

    expect(component.successMessage).toBe(
      'Exam was created successfully.'
    );
  });

  it('should not create an exam without an exam type', () => {
    component.openCreateExamDialog();

    component.examType = '';
    component.examDate = '2026-09-10';
    component.examRoom = 'A101';

    component.createExam();

    expect(examService.create4).not.toHaveBeenCalled();

    expect(component.errorMessage).toBe(
      'Please enter the exam type.'
    );
  });

  it('should not create an exam without an exam date', () => {
    component.openCreateExamDialog();

    component.examType = 'Final';
    component.examDate = '';
    component.examRoom = 'A101';

    component.createExam();

    expect(examService.create4).not.toHaveBeenCalled();

    expect(component.errorMessage).toBe(
      'Please select the exam date.'
    );
  });

  it('should not create an exam without a room', () => {
    component.openCreateExamDialog();

    component.examType = 'Final';
    component.examDate = '2026-09-10';
    component.examRoom = '';

    component.createExam();

    expect(examService.create4).not.toHaveBeenCalled();

    expect(component.errorMessage).toBe(
      'Please enter the exam room.'
    );
  });

  it('should navigate back to courses', () => {
    component.goBack();

    expect(routerMock.navigate).toHaveBeenCalledWith([
      '/courses',
    ]);
  });

  it('should return the student full name', () => {
    expect(component.getStudentName(student)).toBe(
      'Ion Popescu'
    );
  });
});
