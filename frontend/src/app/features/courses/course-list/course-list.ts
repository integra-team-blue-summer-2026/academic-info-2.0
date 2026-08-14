import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';

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

  showCourseForm = false;

  selectedCourse: CourseDto | null = null;

  constructor(private courseService: CourseControllerService) {}

  ngOnInit(): void {
    this.loadCourses();
  }

  loadCourses(): void {
    this.courseService.getAll4().subscribe({
      next: (courses) => {
        this.courses = courses;
        this.filteredCourses = courses;
      },
      error: (error) => {
        console.error('Failed to load courses', error);
      }
    });
  }

  teacherSearchTerm = '';

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
