import { Component, computed, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { TableModule } from 'primeng/table';
import { TagModule } from 'primeng/tag';
import { ButtonModule } from 'primeng/button';
import { ButtonGroupModule } from 'primeng/buttongroup';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { DialogModule } from 'primeng/dialog';
import { ConfirmationService, SharedModule } from 'primeng/api';

import {
  CourseControllerService,
  CourseDto,
  FinalGradeControllerService,
  StudentGradeRowDto,
  TeacherFinalGradeDto,
  YearlyStatisticsDto
} from '../../core/api';

@Component({
  selector: 'teacher-final-grades',
  standalone: true,
  imports: [
    CommonModule,
    TableModule,
    TagModule,
    ButtonModule,
    ButtonGroupModule,
    SharedModule,
    FormsModule,
    ConfirmDialogModule,
    DialogModule
  ],
  providers: [ConfirmationService],
  templateUrl: './teacher-final-grades.html',
  styleUrls: ['./teacher-final-grades.css']
})
export class TeacherFinalGrades implements OnInit {
  teacherId: string = '10000000-0000-0000-0000-000000000001';

  loading = signal<boolean>(false);
  isSaving = signal<boolean>(false);
  allCourses = signal<CourseDto[]>([]);
  gradingSheet = signal<StudentGradeRowDto[]>([]);
  selectedCourseId = signal<string | null>(null);
  selectedStudent = signal<StudentGradeRowDto | null>(null);

  editGradeValue = signal<number | null>(null);
  editProvisional = signal<boolean>(false);

  showStatsDialog = signal<boolean>(false);
  statisticsLoading = signal<boolean>(false);
  yearlyStats = signal<YearlyStatisticsDto[]>([]);

  currentYearStats = signal<YearlyStatisticsDto | null>(null);
  normalAverage = signal<number | null>(null);
  percentageDeviation = signal<number | null>(null);
  alertTriggered = signal<boolean>(false);
  alertMessage = signal<string>('');

  filteredStudents = computed(() => {
    return this.gradingSheet();
  });

  selectedCourseName = computed(() => {
    return this.allCourses().find(c => c.id === this.selectedCourseId())?.courseName ?? '';
  });

  constructor(
    private courseService: CourseControllerService,
    private finalGradeService: FinalGradeControllerService,
    private confirmationService: ConfirmationService
  ) {}

  ngOnInit(): void {
    this.loadCourses();
  }

  protected loadCourses(): void {
    this.loading.set(true);

    this.courseService.getCoursesByTeacherId(this.teacherId).subscribe({
      next: (courses) => {
        this.allCourses.set(courses);
        if (courses.length > 0) {
          const firstCourseId = courses[0].id ?? null;
          this.selectedCourseId.set(firstCourseId);
          this.loadGradingSheet(firstCourseId);
          this.loadStatistics(firstCourseId);
        }
        this.loading.set(false);
      },
      error: () => {
        this.loading.set(false);
      }
    });
  }

  selectCourse(courseId?: string): void {
    if (courseId) {
      this.selectedCourseId.set(courseId);
      this.loadGradingSheet(courseId);
      this.loadStatistics(courseId);
    }
  }

  loadGradingSheet(courseId: string | null): void {
    if (!courseId) return;

    this.loading.set(true);
    this.selectedStudent.set(null);

    this.finalGradeService.getGradingSheetByCourseId(courseId).subscribe({
      next: (sheet) => {
        this.gradingSheet.set(sheet);
        this.loading.set(false);
      },
      error: () => {
        this.loading.set(false);
      }
    });
  }

  loadStatistics(courseId: string | null): void {
    if (!courseId) return;

    this.statisticsLoading.set(true);
    this.finalGradeService.getCourseStatistics(courseId).subscribe({
      next: (stats) => {
        this.yearlyStats.set(stats);
        this.computeStatisticsAndAlerts(stats);
        this.statisticsLoading.set(false);
      },
      error: () => {
        this.statisticsLoading.set(false);
      }
    });
  }

  private computeStatisticsAndAlerts(stats: YearlyStatisticsDto[]): void {
    if (!stats || stats.length === 0) {
      this.currentYearStats.set(null);
      this.normalAverage.set(null);
      this.alertTriggered.set(false);
      return;
    }

    const current = stats[0];
    this.currentYearStats.set(current);

    const previousYears = stats.slice(1, 4);

    if (previousYears.length > 0 && current.averageGrade !== undefined && current.averageGrade !== null) {
      const sum = previousYears.reduce((acc, item) => acc + (item.averageGrade ?? 0), 0);
      const normal = Math.round((sum / previousYears.length) * 100) / 100;
      this.normalAverage.set(normal);

      if (normal > 0) {
        const deviation = Math.round(((current.averageGrade - normal) / normal) * 1000) / 10;
        this.percentageDeviation.set(deviation);

        if (Math.abs(deviation) >= 10) {
          this.alertTriggered.set(true);

          const msg = deviation > 0
            ? `Performance increased by ${deviation}% compared to The Normal (${normal})!`
            : `Performance decreased by ${Math.abs(deviation)}% compared to The Normal (${normal})!`;

          this.alertMessage.set(msg);

        } else {
          this.alertTriggered.set(false);
          this.alertMessage.set('');
        }
      }
    } else {
      this.normalAverage.set(null);
      this.alertTriggered.set(false);
    }
  }

  openStatisticsModal(): void {
    this.showStatsDialog.set(true);
  }

  onSelectStudent(student: StudentGradeRowDto): void {
    if (this.selectedStudent()?.studentId === student.studentId) {
      this.selectedStudent.set(null);
      return;
    }

    this.selectedStudent.set(student);
    this.editGradeValue.set(student.grade ?? null);
    this.editProvisional.set(student.provisional ?? false);
  }

  confirmSaveGrade(): void {
    const current = this.selectedStudent();
    const courseId = this.selectedCourseId();
    const gradeVal = this.editGradeValue();

    if (!current || !courseId || gradeVal === null || gradeVal === undefined) {
      return;
    }

    this.confirmationService.confirm({
      header: 'Confirm Grade',
      acceptLabel: 'Confirm',
      rejectLabel: 'Cancel',
      accept: () => {
        this.saveGrade();
      }
    });
  }

  private saveGrade(): void {
    const current = this.selectedStudent();
    const courseId = this.selectedCourseId();
    const gradeVal = this.editGradeValue();

    if (!current || !courseId || gradeVal === null || gradeVal === undefined) {
      return;
    }

    this.isSaving.set(true);

    const d = new Date();
    const year = d.getFullYear();
    const month = String(d.getMonth() + 1).padStart(2, '0');
    const day = String(d.getDate()).padStart(2, '0');
    const localDateString = `${year}-${month}-${day}`;

    const payload: TeacherFinalGradeDto = {
      id: current.finalGradeId ?? undefined,
      studentId: current.studentId,
      courseId: courseId,
      grade: gradeVal,
      provisional: this.editProvisional(),
      completionDate: localDateString
    };

    this.finalGradeService.createFinalGrade(payload).subscribe({
      next: (savedGrade) => {
        const updatedStudent: StudentGradeRowDto = {
          ...current,
          finalGradeId: savedGrade.id,
          grade: savedGrade.grade,
          provisional: savedGrade.provisional,
          completionDate: savedGrade.completionDate
        };

        this.gradingSheet.update((list) =>
          list.map((s) => (s.studentId === current.studentId ? updatedStudent : s))
        );

        this.selectedStudent.set(updatedStudent);
        this.isSaving.set(false);

        this.loadStatistics(courseId);
      },
      error: () => {
        this.isSaving.set(false);
      }
    });
  }
}
