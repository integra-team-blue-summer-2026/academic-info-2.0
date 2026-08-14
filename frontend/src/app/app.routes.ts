import {Routes} from '@angular/router';
import {Home} from './features/home/home';

export const routes: Routes = [
  {
    path: '',
    component: Home,
  },
  {
    path: 'teachers',
    loadComponent: () => import('./features/teachers/teachers').then(m => m.Teachers),
  }
];
