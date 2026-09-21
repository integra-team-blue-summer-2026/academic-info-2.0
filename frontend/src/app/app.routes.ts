import { Routes } from '@angular/router';
import { Home } from './features/home/home';
import { CourseList } from './features/courses/course-list/course-list';
import { CourseDetails } from './features/courses/course-details/course-details';
import { StudentFinalGradesComponent } from './features/student-final-grades/student-final-grades';
import {TeacherFinalGrades} from './features/teacher-final-grades/teacher-final-grades';
import { StudentOverview } from './features/overviews/student-overview/student-overview';
import { TeacherOverview } from './features/overviews/teacher-overview/teacher-overview';
import {TeacherExamsComponent} from './features/exams-teachers/exams-teachers';
import { FinalGradesComponent } from './features/final-grades/final-grades';
import {Exams} from './features/exams/exams';
import { StudentOverview } from './features/overviews/student-overview/student-overview';
import { TeacherOverview } from './features/overviews/teacher-overview/teacher-overview';
import { StudentThesis } from './features/student-thesis/student-thesis';

export const routes: Routes = [
  {
    path: '',
    component: Home,
  },
  {
    path: 'login',
    loadComponent: () => import('./features/auth/login/login').then(m => m.Login)
  },
  {
    path: 'signup',
    loadComponent: () => import('./features/auth/signup/signup').then(m => m.Signup)
  },
  {
    path: 'teachers',
    loadComponent: () => import('./features/teachers/teachers').then(m => m.Teachers),
  },
  {
    path: 'exams',
    component: Exams,
  },
  {
    path: 'courses',
    component: CourseList,
  },
  {
    path: 'courses/teacher/:id',
    loadComponent: () =>
      import('./features/courses/course-details-teacher/course-details-teacher')
        .then(m => m.CourseDetailsTeacher),
  },
  {
    path: 'courses/:id',
    component: CourseDetails,
  },
  {
    path: 'student/grades',
    component: StudentFinalGradesComponent
  },
  {
    path: 'teacher/grades',
    component: TeacherFinalGrades
  },
  {
    path: 'student/courses',
    component: StudentOverview,
  },
  {
    path: 'teacher/courses',
    component: TeacherOverview,
  },
  {
    path: 'teacher/exams',
    component: TeacherExamsComponent
  },
  {
    path: 'student/thesis',
    component: StudentThesis
  },
  {
    path: 'room-availability',
    loadComponent: () =>
      import('./features/room-availability/room-availability')
        .then(m => m.RoomAvailability),
  },
];
