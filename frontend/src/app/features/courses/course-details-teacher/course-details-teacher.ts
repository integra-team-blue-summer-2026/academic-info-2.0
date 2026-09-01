import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { CourseDto } from '../../../core/api/model/courseDto';
import { StudentDto } from '../../../core/api/model/studentDto';
import { StudentCourseDto } from '../../../core/api/model/studentCourseDto';
import { ExamDto } from '../../../core/api/model/examDto';

import { CourseControllerService } from '../../../core/api/api/courseController.service';
import { StudentControllerService } from '../../../core/api/api/studentController.service';
import { StudentCourseControllerService } from '../../../core/api/api/studentCourseController.service';
import { ExamControllerService } from '../../../core/api/api/examController.service';

import { Button } from 'primeng/button';
import { TableModule } from 'primeng/table';

interface CourseStudent {
  student: StudentDto;
  enrollment: StudentCourseDto;
}

@Component({
  selector: 'app-course-details-teacher',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    Button,
    TableModule
  ],
  templateUrl: './course-details-teacher.html',
  styleUrl: './course-details-teacher.css'
})
export class CourseDetailsTeacher implements OnInit {

  course: CourseDto | null = null;

  students: CourseStudent[] = [];
  availableStudents: StudentDto[] = [];

  exams: ExamDto[] = [];

  loadingCourse = true;
  loadingStudents = true;
  loadingExams = true;
  loadingAvailableStudents = false;

  errorMessage = '';
  successMessage = '';

  // Add student dialog
  addStudentDialogVisible = false;
  selectedStudentId = '';

  // Create exam dialog
  createExamDialogVisible = false;

  examType = '';
  examDate = '';
  examRoom = '';

  submittingStudent = false;
  submittingExam = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private courseService: CourseControllerService,
    private studentService: StudentControllerService,
    private studentCourseService: StudentCourseControllerService,
    private examService: ExamControllerService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    const courseId = this.route.snapshot.paramMap.get('id');

    if (!courseId) {
      this.errorMessage = 'Course ID was not provided.';
      this.loadingCourse = false;
      this.loadingStudents = false;
      this.loadingExams = false;
      return;
    }

    this.loadCourse(courseId);
    this.loadStudents(courseId);
    this.loadExams(courseId);
  }

  private loadCourse(courseId: string): void {
    this.loadingCourse = true;

    this.courseService.getById4(courseId).subscribe({
      next: (course) => {
        this.course = course;
        this.loadingCourse = false;

        this.cdr.markForCheck();
      },
      error: (error) => {
        console.error('Failed to load course', error);

        this.errorMessage = 'Failed to load course.';
        this.loadingCourse = false;

        this.cdr.markForCheck();
      }
    });
  }

  private loadStudents(courseId: string): void {
    this.loadingStudents = true;

    this.studentCourseService.getByCourseId(courseId).subscribe({
      next: (enrollments) => {
        if (!enrollments || enrollments.length === 0) {
          this.students = [];
          this.loadingStudents = false;

          this.cdr.markForCheck();
          return;
        }

        let completedRequests = 0;
        const loadedStudents: CourseStudent[] = [];

        enrollments.forEach((enrollment) => {
          if (!enrollment.studentId) {
            completedRequests++;

            if (completedRequests === enrollments.length) {
              this.students = loadedStudents;
              this.loadingStudents = false;
              this.cdr.markForCheck();
            }

            return;
          }

          this.studentService.getById1(enrollment.studentId).subscribe({
            next: (student) => {
              loadedStudents.push({
                student,
                enrollment
              });

              completedRequests++;

              if (completedRequests === enrollments.length) {
                this.students = loadedStudents;
                this.loadingStudents = false;

                this.cdr.markForCheck();
              }
            },
            error: (error) => {
              console.error(
                `Failed to load student ${enrollment.studentId}`,
                error
              );

              completedRequests++;

              if (completedRequests === enrollments.length) {
                this.students = loadedStudents;
                this.loadingStudents = false;

                this.cdr.markForCheck();
              }
            }
          });
        });

        if (completedRequests === enrollments.length) {
          this.students = loadedStudents;
          this.loadingStudents = false;

          this.cdr.markForCheck();
        }
      },
      error: (error) => {
        console.error('Failed to load course students', error);

        this.students = [];
        this.loadingStudents = false;

        this.cdr.markForCheck();
      }
    });
  }

  private loadExams(courseId: string): void {
    this.loadingExams = true;

    // The generated ExamControllerService exposes getAll4()
    this.examService.getAll4().subscribe({
      next: (exams) => {
        this.exams = (exams || []).filter(
          exam => exam.courseId === courseId
        );

        this.loadingExams = false;

        this.cdr.markForCheck();
      },
      error: (error) => {
        console.error('Failed to load exams', error);

        this.exams = [];
        this.loadingExams = false;

        this.cdr.markForCheck();
      }
    });
  }

  /**
   * Opens the Add Student dialog and loads all existing students.
   */
  openAddStudentDialog(): void {
    this.selectedStudentId = '';
    this.successMessage = '';
    this.errorMessage = '';

    this.loadingAvailableStudents = true;
    this.addStudentDialogVisible = true;

    this.studentService.getAll1().subscribe({
      next: (students) => {
        this.availableStudents = students || [];
        this.loadingAvailableStudents = false;

        this.cdr.markForCheck();
      },
      error: (error) => {
        console.error('Failed to load available students', error);

        this.availableStudents = [];
        this.loadingAvailableStudents = false;
        this.errorMessage = 'Failed to load available students.';

        this.cdr.markForCheck();
      }
    });
  }

  /**
   * Returns only students that are not already enrolled in the current course.
   */
  get availableStudentsForCourse(): StudentDto[] {
    const enrolledStudentIds = new Set(
      this.students
        .map(courseStudent => courseStudent.student.id)
        .filter((id): id is string => !!id)
    );

    return this.availableStudents.filter(
      student => !!student.id && !enrolledStudentIds.has(student.id)
    );
  }

  /**
   * Adds an existing student to the current course.
   */
  addSelectedStudent(): void {
    const courseId = this.course?.id;
    const studentId = this.selectedStudentId;

    if (!courseId || !studentId) {
      this.errorMessage = 'Please select a student.';
      return;
    }

    const alreadyEnrolled = this.students.some(
      courseStudent => courseStudent.student.id === studentId
    );

    if (alreadyEnrolled) {
      this.errorMessage = 'This student is already enrolled in the course.';
      return;
    }

    const enrollment: StudentCourseDto = {
      courseId,
      studentId
    };

    this.submittingStudent = true;
    this.errorMessage = '';
    this.successMessage = '';

    this.studentCourseService.create3(enrollment).subscribe({
      next: () => {
        this.submittingStudent = false;
        this.addStudentDialogVisible = false;
        this.selectedStudentId = '';

        this.successMessage = 'Student was added to the course.';

        // Refresh the students table
        this.loadStudents(courseId);

        this.cdr.markForCheck();
      },
      error: (error) => {
        console.error('Failed to add student to course', error);

        this.submittingStudent = false;
        this.errorMessage = 'Failed to add student to the course.';

        this.cdr.markForCheck();
      }
    });
  }

  closeAddStudentDialog(): void {
    if (this.submittingStudent) {
      return;
    }

    this.addStudentDialogVisible = false;
    this.selectedStudentId = '';
  }

  /**
   * Opens the Create Exam dialog.
   */
  openCreateExamDialog(): void {
    this.examType = '';
    this.examDate = '';
    this.examRoom = '';

    this.successMessage = '';
    this.errorMessage = '';

    this.createExamDialogVisible = true;
  }

  closeCreateExamDialog(): void {
    if (this.submittingExam) {
      return;
    }

    this.createExamDialogVisible = false;

    this.examType = '';
    this.examDate = '';
    this.examRoom = '';
  }

  /**
   * Creates an exam for the current course.
   */
  createExam(): void {
    const courseId = this.course?.id;

    if (!courseId) {
      this.errorMessage = 'Course ID was not provided.';
      return;
    }

    if (!this.examType.trim()) {
      this.errorMessage = 'Please enter the exam type.';
      return;
    }

    if (!this.examDate) {
      this.errorMessage = 'Please select the exam date.';
      return;
    }

    if (!this.examRoom.trim()) {
      this.errorMessage = 'Please enter the exam room.';
      return;
    }

    const exam: ExamDto = {
      courseId,
      examType: this.examType.trim(),
      examDate: this.examDate,
      room: this.examRoom.trim()
    };

    this.submittingExam = true;
    this.errorMessage = '';
    this.successMessage = '';

    this.examService.create4(exam).subscribe({
      next: () => {
        this.submittingExam = false;
        this.createExamDialogVisible = false;

        this.examType = '';
        this.examDate = '';
        this.examRoom = '';

        this.successMessage = 'Exam was created successfully.';

        // Refresh the exams table
        this.loadExams(courseId);

        this.cdr.markForCheck();
      },
      error: (error) => {
        console.error('Failed to create exam', error);

        this.submittingExam = false;
        this.errorMessage = 'Failed to create exam.';

        this.cdr.markForCheck();
      }
    });
  }

  goBack(): void {
    this.router.navigate(['/courses']);
  }

  getStudentName(student: StudentDto): string {
    return `${student.firstName ?? ''} ${student.lastName ?? ''}`.trim();
  }
}
