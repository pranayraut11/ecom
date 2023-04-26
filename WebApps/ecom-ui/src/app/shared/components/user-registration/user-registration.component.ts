import { Component, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { CreateUser } from '../../models/CreateUser.model';
import { UserCredential } from '../../models/UserCredential';
import { UserRestService } from '../../services/rest-services/user-rest-service';

@Component({
  selector: 'app-user-registration',
  templateUrl: './user-registration.component.html',
  styleUrls: ['./user-registration.component.css']
})
export class UserRegistrationComponent implements OnInit {

  constructor(private userRest: UserRestService) { }

  ngOnInit(): void {
  }
  
  register(registrationForm : NgForm){
    const registrationFormData = registrationForm.value.registerUserData;
    console.log(registrationFormData.first_name);
    let credential = new UserCredential('password',registrationFormData.password);
    const userCredentials = new Array<UserCredential>;
    userCredentials.push(credential);
    let createUser = new CreateUser(registrationFormData.email,registrationFormData.email,registrationFormData.first_name,registrationFormData.last_name,userCredentials,true);
    this.userRest.registerUser(createUser).subscribe((response)=> {
      console.log("User created successfully");
    });
  }

}
