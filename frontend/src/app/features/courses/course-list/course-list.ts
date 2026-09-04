import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

import { CourseDto } from '../../../core/api/models/courseDto';
import { CourseControllerService } from '../../../core/api/services/courseController.service';

import { TableModule } from 'primeng/table';
import { Button } from 'primeng/button';
import { InputText } from 'primeng/inputtext';
import { Dialog } from 'primeng/dialog';

import { CourseForm } from '../course-form/course-form';

@Component({
  selector: 'app-course-list',
  imports: [
    FormsModule,
    TableModule,
    Button,
    InputText,
    Dialog,
    CourseForm
  ],
  templateUrl: './course-list.html',
  styleUrl: './course-list.css'
})
export class CourseList implements OnInit {

  courses: CourseDto[] = [];

  filteredCourses: CourseDto[] = [];

  searchTerm = '';

  teacherSearchTerm = '';

  showCourseForm = false;

  selectedCourse: CourseDto | null = null;

  constructor(
    private courseService: CourseControllerService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadCourses();
  }

  loadCourses(): void {
    this.courseService.getAll4().subscribe({
      next: (courses) => {
        this.courses = courses;
        this.filteredCourses = courses;
        this.cdr.markForCheck();
      },
      error: (error) => {
        console.error('Failed to load courses', error);
      }
    });
  }

  filterCourses(): void {
    const term = this.searchTerm.trim().toLowerCase();
    const teacherTerm = this.teacherSearchTerm.trim().toLowerCase();

    this.filteredCourses = this.courses.filter(course => {
      const matchesCourse =
        !term ||
        course.courseName?.toLowerCase().includes(term) ||
        course.description?.toLowerCase().includes(term) ||
        course.syllabus?.toLowerCase().includes(term);

      const matchesTeacher =
        !teacherTerm ||
        course.teacherId?.toLowerCase().includes(teacherTerm);

      return matchesCourse && matchesTeacher;
    });
  }

  viewCourseDetails(course: CourseDto): void {
    if (!course.id) {
      return;
    }

    this.router.navigate(['/courses', course.id]);
  }

  openAddCourse(): void {
    this.selectedCourse = null;
    this.showCourseForm = true;
  }

  openEditCourse(course: CourseDto): void {
    this.selectedCourse = course;
    this.showCourseForm = true;
  }

  deleteCourse(course: CourseDto): void {
    if (!course.id) {
      return;
    }

    const confirmed = window.confirm(
      `Are you sure you want to delete "${course.courseName}"?`
    );

    if (!confirmed) {
      return;
    }

    this.courseService.delete4(course.id).subscribe({
      next: () => {
        this.loadCourses();
      },
      error: (error) => {
        console.error('Failed to delete course', error);
      }
    });
  }

  onCourseSaved(): void {
    this.showCourseForm = false;
    this.selectedCourse = null;
    this.loadCourses();
  }

  closeCourseForm(): void {
    this.showCourseForm = false;
    this.selectedCourse = null;
  }
}
