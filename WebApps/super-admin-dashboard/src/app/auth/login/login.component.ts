import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { AuthService } from '../auth.service';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule]
})
export class LoginComponent {
  form: FormGroup;
  error: string = '';
  isLoading: boolean = false;

  constructor(
    private fb: FormBuilder, 
    private auth: AuthService, 
    private router: Router
  ) {
    this.form = this.fb.group({
      username: ['', [Validators.required, Validators.minLength(3)]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });
  }

  async login() {
    if (this.form.invalid) {
      return;
    }

    this.isLoading = true;
    this.error = '';

    try {
      const result = await this.auth.login(
        this.form.value.username, 
        this.form.value.password
      );
      
      if (result) {
        this.router.navigate(['/']);
      } else {
        this.error = 'Invalid credentials';
      }
    } catch (err) {
      this.error = 'An error occurred during login';
    } finally {
      this.isLoading = false;
    }
  }
}
