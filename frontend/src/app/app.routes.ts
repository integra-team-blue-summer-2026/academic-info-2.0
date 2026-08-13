import { Routes } from '@angular/router';
import { Home } from './features/home/home';
import { CourseList } from './features/courses/course-list/course-list';

export const routes: Routes = [
  {
    path: '',
    component: Home,
  },
  {
    path: 'courses',
    component: CourseList,
  }
];
