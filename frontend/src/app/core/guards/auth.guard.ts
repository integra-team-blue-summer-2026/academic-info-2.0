import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

/**
 * Route guard that redirects unauthenticated users to /login.
 *
 * Used on any route that requires the user to be logged in.
 * If the user has a valid token in localStorage, they pass through.
 * If not, they are sent to /login and the navigation is cancelled.
 *
 * This is a functional guard (Angular 14.2+), which is the modern approach
 * and avoids the need for a guard class and providedIn injection.
 */
export const authGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (authService.isLoggedIn()) {
    return true;
  }

  return router.createUrlTree(['/login']);
};
