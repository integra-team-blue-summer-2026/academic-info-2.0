import { Routes } from '@angular/router';
import { Home } from './features/home/home';
import { CourseList } from './features/courses/course-list/course-list';
import { CourseDetails } from './features/courses/course-details/course-details';
import { StudentOverview } from './features/overviews/student-overview/student-overview';
import { TeacherOverview } from './features/overviews/teacher-overview/teacher-overview';

export const routes: Routes = [
  {
    path: '',
    component: Home,
  },
  {
    path: 'courses',
    component: CourseList,
  },
  {
    path: 'courses/:id',
    component: CourseDetails,
  },
  {
    path: 'student/courses',
    component: StudentOverview,
  },
  {
    path: 'teacher/courses',
    component: TeacherOverview,
  }
];
