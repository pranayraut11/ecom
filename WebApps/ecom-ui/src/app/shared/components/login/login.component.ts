import { Component, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { Login } from '../../models/Login.model';
import { AuthRestService } from '../../services/rest-services/auth-rest-service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

  constructor(private authRest: AuthRestService) { }

  ngOnInit(): void {
  }

  login(form: NgForm){
    const value = form.value;
    const login = new Login(value.username,value.password);
    this.authRest.login(login).subscribe(response=>{console.log(response)});
  }
}
