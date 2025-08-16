import { Component, OnInit } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { AuthService } from 'src/app/core/auth/services/auth.service';
import { AUTH_TOKEN } from 'src/app/shared/constants/AuthConst';
import { Login } from 'src/app/shared/models/Login.model';
import { CommonModule } from '@angular/common';
import { UserRegistrationComponent } from '../../user-registration/user-registration.component';

declare var $: any;
@Component({
  selector: 'app-login-popup',
  templateUrl: './login-popup.component.html',
  styleUrls: ['./login-popup.component.css'],
  standalone: true,
  imports: [
    CommonModule,
    FormsModule
  ]
})

export class LoginPopupComponent implements OnInit {
  isAuthenticated: boolean = false;
  private userSub: Subscription;
  public show: boolean = true;
  errorMessage: string;
  showErrorMessage: boolean = false;
  hasRole: string;
  constructor(private authService: AuthService, private router: Router) { }

  ngOnInit(): void {
  }

  login(form: NgForm) {

    const value = form.value;
    const login = new Login(value.username, value.password);

    this.authService.login(login).subscribe({
      next: (response) => {
        if (response.roles.includes("seller")) {
          $('#loginModel').modal('hide');
          this.show = false;
          window.location.href = "/seller/dashboard"
        } else {
          console.log(localStorage.getItem(AUTH_TOKEN));
          $('#loginModel').modal('hide');
          this.show = false;
          window.location.href = this.router.url
        }
      },
      complete: () => {

        console.log("Req complete");
      }
      ,
      error: (error) => {
        console.log(error);
        this.errorMessage = error;

        this.showErrorMessage = true;
      }
    });

  }

}
