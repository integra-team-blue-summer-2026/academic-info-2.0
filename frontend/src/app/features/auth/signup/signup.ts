import { Component, signal, computed } from '@angular/core';
import {
  AbstractControl,
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  ValidationErrors,
  ValidatorFn,
  Validators,
} from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { AuthService } from '../../../core/services/auth.service';

/**
 * Custom validator that mirrors the server-side password policy.
 * Validates on the client first for instant feedback — the server
 * validates again as the source of truth.
 *
 * Rules:
 *  - At least 8 characters
 *  - At least one uppercase letter
 *  - At least one lowercase letter
 *  - At least one digit
 *  - At least one special character
 */
const passwordPolicyValidator: ValidatorFn = (control: AbstractControl): ValidationErrors | null => {
  const value: string = control.value ?? '';
  const errors: Record<string, boolean> = {};

  if (value.length < 8) errors['minLength'] = true;
  if (!/[A-Z]/.test(value)) errors['uppercase'] = true;
  if (!/[a-z]/.test(value)) errors['lowercase'] = true;
  if (!/[0-9]/.test(value)) errors['digit'] = true;
  if (!/[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]/.test(value)) errors['special'] = true;

  return Object.keys(errors).length ? errors : null;
};

/**
 * Validator that ensures the confirmPassword field matches password.
 */
const passwordMatchValidator: ValidatorFn = (group: AbstractControl): ValidationErrors | null => {
  const password = group.get('password')?.value;
  const confirm = group.get('confirmPassword')?.value;
  return password === confirm ? null : { passwordMismatch: true };
};

/**
 * Signup page component.
 *
 * Validates password complexity on the client for immediate user feedback,
 * then delegates registration to {@link AuthService}. On success, the user
 * is redirected to /login with a success flag so the login page can show a
 * confirmation message.
 */
@Component({
  selector: 'app-signup',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule, RouterLink],
  templateUrl: './signup.html',
  styleUrl: './signup.css',
})
export class Signup {
  form: FormGroup;
  errorMessage = signal<string | null>(null);
  isLoading = signal(false);
  showPassword = signal(false);
  showConfirm = signal(false);

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router,
  ) {
    this.form = this.fb.group(
      {
        username: ['', [Validators.required, Validators.minLength(3)]],
        password: ['', [Validators.required, passwordPolicyValidator]],
        confirmPassword: ['', Validators.required],
      },
      { validators: passwordMatchValidator },
    );
  }

  /** Checks whether a specific password policy rule is satisfied. */
  rulePass(rule: string): boolean {
    const ctrl = this.form.get('password');
    if (!ctrl || !ctrl.value) return false;
    return !ctrl.hasError(rule);
  }

  togglePassword(): void {
    this.showPassword.update((v) => !v);
  }

  toggleConfirm(): void {
    this.showConfirm.update((v) => !v);
  }

  onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.isLoading.set(true);
    this.errorMessage.set(null);

    const { username, password } = this.form.value;

    this.authService.register(username, password).subscribe({
      next: () => {
        this.router.navigate(['/login']);
      },
      error: (err: HttpErrorResponse) => {
        this.isLoading.set(false);
        if (err.status === 409) {
          this.errorMessage.set('That username is already taken. Please choose another.');
        } else if (err.status === 400) {
          this.errorMessage.set(err.error?.detail ?? 'Invalid input. Please check your details.');
        } else {
          this.errorMessage.set('Something went wrong. Please try again.');
        }
      },
    });
  }

  get usernameCtrl() {
    return this.form.get('username')!;
  }

  get passwordCtrl() {
    return this.form.get('password')!;
  }

  get confirmCtrl() {
    return this.form.get('confirmPassword')!;
  }
}
