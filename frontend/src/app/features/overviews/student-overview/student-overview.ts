import {
  ChangeDetectorRef,
  Component,
  OnInit
} from '@angular/core';

import { CourseDto } from '../../../core/api/models/courseDto';
import { CourseControllerService } from '../../../core/api/services/courseController.service';

import { TableModule } from 'primeng/table';

@Component({
  selector: 'app-student-overview',
  imports: [
    TableModule
  ],
  templateUrl: './student-overview.html',
  styleUrl: './student-overview.css'
})
export class StudentOverview implements OnInit {

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
}
