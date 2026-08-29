import {
  ChangeDetectorRef,
  Component,
  OnInit
} from '@angular/core';

import { CourseDto } from '../../../core/api/models/courseDto';
import { CourseControllerService } from '../../../core/api/services/courseController.service';

import { TableModule } from 'primeng/table';
import { CardModule } from 'primeng/card';

@Component({
  selector: 'app-teacher-overview',
  imports: [
    TableModule,
    CardModule
  ],
  templateUrl: './teacher-overview.html',
  styleUrl: './teacher-overview.css'
})
export class TeacherOverview implements OnInit {

  courses: CourseDto[] = [];

  constructor(
    private courseService: CourseControllerService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadCourses();
  }

  loadCourses(): void {
    this.courseService.getAll4().subscribe({
      next: (courses) => {
        this.courses = courses;
        this.cdr.detectChanges();
      },
      error: (error) => {
        console.error('Failed to load courses', error);
      }
    });
  }

  get totalCourses(): number {
    return this.courses.length;
  }
}
