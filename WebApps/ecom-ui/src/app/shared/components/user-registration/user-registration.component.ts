import { Component, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';

@Component({
  selector: 'app-user-registration',
  templateUrl: './user-registration.component.html',
  styleUrls: ['./user-registration.component.css']
})
export class UserRegistrationComponent implements OnInit {

  constructor() { }

  ngOnInit(): void {
  }

  register(registrationForm : NgForm){
    const registrationFormData = registrationForm.value;
    console.log(registrationFormData.first_name);
  }

}
