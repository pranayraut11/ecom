import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, NgForm,Validators ,AbstractControl,ReactiveFormsModule} from '@angular/forms';
import { CreateUser } from '../../models/CreateUser.model';
import { UserCredential } from '../../models/UserCredential';
import { UserRestService } from '../../services/rest-services/user-rest-service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-user-registration',
  templateUrl: './user-registration.component.html',
  styleUrls: ['./user-registration.component.css'],
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule
  ]
})
export class UserRegistrationComponent implements OnInit {
  userForm: FormGroup;
  submitted = false;
  constructor(private userRest: UserRestService,private fb: FormBuilder) { }
  validationAlter = 'alert alert-danger';
  showAlter = false;
  alterMessage="";
  ngOnInit(): void {
   this.userForm = this.fb.group(
      {
        firstName: ['', [Validators.required, Validators.minLength(2)]],
        secondName: ['', [Validators.required]],
        email: ['', [Validators.required, Validators.email]],
        password: ['', [Validators.required, Validators.minLength(6)]],
        confirmPassword: ['', [Validators.required]],
        mobile: ['', [Validators.required, Validators.pattern(/^[0-9]{10}$/)]],
      },
      { validators: this.passwordMatchValidator } // custom validator
    );
  }
  
  
  get f(): { [key: string]: AbstractControl } {
    return this.userForm.controls;
  }

    passwordMatchValidator(group: AbstractControl) {
    const password = group.get('password')?.value;
    const confirmPassword = group.get('confirmPassword')?.value;
    return password === confirmPassword ? null : { mismatch: true };
  }


  onSubmit(){
     this.submitted = true;
     if (this.userForm.invalid) {
      return;
    }
    const registrationFormData = this.userForm.value;
    console.log(registrationFormData.first_name);
    let createUser = new CreateUser(registrationFormData.email,registrationFormData.email,registrationFormData.first_name,registrationFormData.last_name,registrationFormData.password,true,registrationFormData.mobile);
    this.showAlter = true;
    this.userRest.registerUser(createUser).subscribe({ next : (response)=> {
      console.log("User created successfully");
      this.validationAlter = 'alert alert-success';
      this.alterMessage = "User created successfully";
    },error : (er)=>{
      this.alterMessage = er.error.message;
      console.log("Error occurred while creating user"+JSON.stringify(er));
    }});
  }
  
}
