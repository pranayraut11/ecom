import { Component, Input, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { from, Subscription } from 'rxjs';
import { AuthService } from 'src/app/core/core/auth/Auth-Service';
import { AUTH_TOKEN } from '../../constants/AuthConst';
import { Login } from '../../models/Login.model';


import { AuthRestService } from '../../services/rest-services/auth-rest-service';
declare var $: any;
@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})

export class LoginComponent implements OnInit {
  isAuthenticated: boolean = false;
  private userSub: Subscription;
  constructor(private authService: AuthService) { }
  public show: boolean = true;
  errorMessage: string;
  showErrorMessage: boolean = false;
  ngOnInit(): void {
    console.log("is " + this.isAuthenticated);
    this.userSub = this.authService.user.subscribe(user => {
      console.log("in header" + this.isAuthenticated);
      this.isAuthenticated = !!user;
      console.log(!user);
      console.log(!!user);
    });
  }

  login(form: NgForm) {

    const value = form.value;
    const login = new Login(value.username, value.password);

    this.authService.login(login).subscribe({
      next: (response) => {

        console.log(localStorage.getItem(AUTH_TOKEN));
        $('#loginModel').modal('hide');
        this.show = false;
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

  logout() {
    console.log("logout")
    this.authService.logout().subscribe({
      next: (response) => {
        console.log(response);
        localStorage.removeItem(AUTH_TOKEN)
        this.isAuthenticated = false;
      },
      error: (error) => {
        localStorage.removeItem(AUTH_TOKEN)
      }
    }
    );
  }
  ngOnDestroy() {
    this.userSub.unsubscribe();
  }
}
