import { Routes } from '@angular/router';
import { Home } from './features/home/home';
import { CourseList } from './features/courses/course-list/course-list';
import { CourseDetails } from './features/courses/course-details/course-details';

export const routes: Routes = [
  {
    path: '',
    component: Home,
  },
  {
    path: 'teachers',
    loadComponent: () =>
      import('./features/teachers/teachers').then(m => m.Teachers),
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
];
