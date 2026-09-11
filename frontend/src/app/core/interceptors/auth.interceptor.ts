import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';

/**
 * HTTP interceptor that transparently attaches the JWT to every outgoing request.
 *
 * WHY an interceptor?
 *   Without an interceptor, every Angular service making HTTP calls would need
 *   to manually add the Authorization header. An interceptor centralises this in
 *   one place — changing auth logic only requires editing this file.
 *
 * HOW it works:
 *   Angular's functional interceptor API (v15+) receives the request and the
 *   next handler. We clone the request (requests are immutable) to add the header,
 *   then pass the clone to the next handler.
 *
 *   If there is no token (user is not logged in), the request is forwarded
 *   unchanged — the server will return 401 for protected routes.
 */
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const token = inject(AuthService).getToken();

  if (token) {
    const authenticatedReq = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`,
      },
    });
    return next(authenticatedReq);
  }

  return next(req);
};
