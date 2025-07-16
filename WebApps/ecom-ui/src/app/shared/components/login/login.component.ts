import { Component, Input, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { Router } from '@angular/router';
import { from, Subscription } from 'rxjs';
import { AuthService } from 'src/app/core/core/auth/Auth-Service';
import { AUTH_TOKEN } from '../../constants/AuthConst';
import { Login } from '../../models/Login.model';
import { AuthRestService } from '../../services/rest-services/auth-rest-service';
import { CommonModule } from '@angular/common';
import { LoginPopupComponent } from './login-popup/login-popup.component';

declare var $: any;
@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
  standalone: true,
  imports: [
    CommonModule,
    LoginPopupComponent
  ]
})

export class LoginComponent implements OnInit {

  isAuthenticated: boolean = false;
  private userSub: Subscription;
  constructor(private authService: AuthService, private router: Router) { }
  public show: boolean = true;
  errorMessage: string;
  showErrorMessage: boolean = false;
  hasRole: string;
  ngOnInit(): void {
    console.log("is " + this.isAuthenticated);
    this.userSub = this.authService.user.subscribe(user => {
      this.isAuthenticated = !!user;
    });
  }
  
  logout() {

    console.log("logout")
    this.authService.logout().subscribe({
      next: (response) => {
        console.log(response);
        this.isAuthenticated = false;

      },
      error: (error) => {

        this.isAuthenticated = false;
      }
    }
    );
    localStorage.removeItem(AUTH_TOKEN);
    this.router.navigate(["/"]);
    window.location.href = "/home"
    this.userSub.unsubscribe();
  }
  ngOnDestroy() {
    this.userSub.unsubscribe();
  }
}
