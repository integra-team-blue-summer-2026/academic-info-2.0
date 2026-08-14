import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { ActivatedRoute, Router } from '@angular/router';

import { CardModule } from 'primeng/card';
import { TableModule } from 'primeng/table';
import { Button } from 'primeng/button';
import { Tag } from 'primeng/tag';

interface Course {
  id: string;
  teacherId: string;
  courseName: string;
  syllabus: string;
  credits: number;
  description: string;
}

interface Exam {
  id: string;
  courseId: string;
  examType: string;
  examDate: string;
  room: string;
}

@Component({
  selector: 'app-course-details',
  imports: [
    CommonModule,
    CardModule,
    TableModule,
    Button,
    Tag
  ],
  templateUrl: './course-details.html',
  styleUrl: './course-details.css'
})
export class CourseDetails implements OnInit {

  course: Course | null = null;
  exams: Exam[] = [];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private http: HttpClient,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    const courseId = this.route.snapshot.paramMap.get('id');

    if (courseId) {
      this.loadCourse(courseId);
      this.loadExams(courseId);
    }
  }

  loadCourse(courseId: string): void {
    this.http
      .get<Course>(`http://localhost:8080/api/courses/${courseId}`)
      .subscribe({
        next: (course) => {
          this.course = course;
          this.cdr.markForCheck();
        },
        error: (error) => {
          console.error('Failed to load course', error);
        }
      });
  }

  loadExams(courseId: string): void {
    this.http
      .get<Exam[]>('http://localhost:8080/api/exams')
      .subscribe({
        next: (exams) => {
          this.exams = exams.filter(exam => exam.courseId === courseId);
          this.cdr.markForCheck();
        },
        error: (error) => {
          console.error('Failed to load exams', error);
        }
      });
  }

  goBack(): void {
    this.router.navigate(['/courses']);
  }
}
