import {Routes} from '@angular/router';
import {Home} from './features/home/home';

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
  }
];
