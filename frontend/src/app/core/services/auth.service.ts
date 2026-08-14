import { Injectable, signal, computed } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { Router } from '@angular/router';

interface LoginResponse {
  token: string;
}

/**
 * AuthService — manages JWT token lifecycle.
 *
 * The token is stored in localStorage (as required by the story).
 * A reactive `isLoggedIn` signal derived from the token state lets
 * components and guards react when the user logs in or out.
 *
 * WHY localStorage?
 *   The story explicitly requires localStorage. In production you would
 *   consider HttpOnly cookies for XSS resistance, but that requires
 *   server-side cookie management which is out of scope here.
 */
@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly TOKEN_KEY = 'auth_token';
  private readonly API_BASE = 'http://localhost:8080/api/auth';

  /** Reactive token signal — updated on login/logout */
  private readonly _token = signal<string | null>(localStorage.getItem(this.TOKEN_KEY));

  /** True if a token is currently stored */
  readonly isLoggedIn = computed(() => this._token() !== null);

  constructor(private http: HttpClient, private router: Router) {}

  /**
   * Calls POST /api/auth/login, stores the returned JWT in localStorage,
   * and updates the reactive token signal.
   */
  login(username: string, password: string): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.API_BASE}/login`, { username, password }).pipe(
      tap((response) => {
        localStorage.setItem(this.TOKEN_KEY, response.token);
        this._token.set(response.token);
      }),
    );
  }

  /**
   * Calls POST /api/auth/register.
   * On success, the caller should redirect to /login.
   */
  register(username: string, password: string): Observable<void> {
    return this.http.post<void>(`${this.API_BASE}/register`, { username, password });
  }

  /**
   * Removes the JWT from localStorage and the reactive signal,
   * then navigates to /login.
   */
  logout(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    this._token.set(null);
    this.router.navigate(['/login']);
  }

  /** Returns the raw JWT string, or null if not logged in. */
  getToken(): string | null {
    return this._token();
  }
}
