import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-update-profile',
  templateUrl: './update-profile.component.html',
  styleUrls: ['./update-profile.component.css']
})
export class UpdateProfileComponent implements OnInit {

  editInfoText = "Edit";
  editEmailText = "Edit";
  editMobileText = "Edit";
  saveInfoButton = false;
  saveEmailButton = false;
  saveMobileButton = false;
  hideButton = false;
  constructor() { }

  ngOnInit(): void {
  }

  personalInfoEdit() {
    console.log("personalInfoEdit clicked")
    if (this.editInfoText === "Edit") {
      this.editInfoText = "Cancel";
      this.saveInfoButton = true;
    }else{
      this.editInfoText = "Edit";
      this.saveInfoButton = false;
    }
  }
  emailEdit() {
    console.log("email clicked")
    if (this.editEmailText === "Edit") {
      this.editEmailText = "Cancel";
      this.saveEmailButton = true;
    }else{
      this.editEmailText = "Edit";
      this.saveEmailButton = false;
    }
  }
  mobileNumberEdit() {
    console.log("mobile number clicked")
    if (this.editMobileText === "Edit") {
      this.editMobileText = "Cancel";
      this.saveMobileButton = true;
    }else{
      this.editMobileText = "Edit";
      this.saveMobileButton = false;
    }
  }

  enableSaveButton(editText:string){
    
  }
}
