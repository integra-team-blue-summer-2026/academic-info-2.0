import { Component, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';

import { TableModule } from 'primeng/table';
import { TagModule } from 'primeng/tag';
import { ButtonModule } from 'primeng/button';
import { ButtonGroupModule } from 'primeng/buttongroup';
import { SharedModule } from 'primeng/api';

import { StudentFinalGradeDto } from '../../core/api/models/studentFinalGradeDto';
import { FinalGradeControllerService } from '../../core/api/services/finalGradeController.service';

@Component({
  selector: 'app-final-grades',
  standalone: true,
  imports: [
    CommonModule,
    TableModule,
    TagModule,
    ButtonModule,
    ButtonGroupModule,
    SharedModule
  ],
  templateUrl: './final-grades.html',
  styleUrls: ['./final-grades.css']
})
export class FinalGradesComponent implements OnInit {

  studentId: string = '11111111-1111-1111-1111-111111111111';

  allGrades = signal<StudentFinalGradeDto[]>([]);
  loading = signal<boolean>(false);
  selectedSemester = signal<number>(-1);

  semesterOptions = computed(() => {
    const grades = this.allGrades();
    const unique = Array.from(
      new Set(grades.map(g => g.semester).filter((s): s is number => s !== undefined && s !== null))
    ).sort((a, b) => a - b);

    return [
      ...unique.map(s => ({ label: `Semester ${s}`, value: s }))
    ];
  });

  filteredGrades = computed(() => {
    const sem = this.selectedSemester();
    const grades = this.allGrades();
    if (sem === -1) return grades;
    return grades.filter(g => g.semester === sem);
  });

  constructor(private finalGradeService: FinalGradeControllerService) {}

  ngOnInit(): void {
    this.loadGrades();
  }

  protected loadGrades(): void {
    this.loading.set(true);

    this.finalGradeService.getFinalGradesByStudentId(this.studentId).subscribe({
      next: (grades) => {
        this.allGrades.set(grades);

        if (grades && grades.length > 0) {
          const validSemesters = grades
            .map(g => g.semester)
            .filter((s): s is number => s !== undefined && s !== null);

          if (validSemesters.length > 0) {
            const latestSemester = Math.max(...validSemesters);
            this.selectedSemester.set(latestSemester);
          }
        }

        this.loading.set(false);
      },
      error: () => {
        this.loading.set(false);
      }
    });
  }

  semesterAverage = computed<string>(() => {
    const grades = this.filteredGrades();
    if (!grades || grades.length === 0) return '-';

    let totalPoints = 0;
    let totalCredits = 0;

    for (const g of grades) {
      const gradeNum = Number(g.grade);
      const creditsNum = Number(g.credits);

      totalPoints += gradeNum * creditsNum;
      totalCredits += creditsNum;
    }

    return totalCredits > 0 ? (totalPoints / totalCredits).toFixed(2) : '-';
  });

  selectSemester(sem: number): void {
    this.selectedSemester.set(sem);
  }
}
