import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, NgForm } from '@angular/forms';
import { NotificationBusService } from '../../../../core/services/notification-bus.service';

interface UserProfile {
  firstName: string;
  lastName: string;
  email: string;
  mobileNumber: string;
  emailNotifications: boolean;
  smsNotifications: boolean;
}

@Component({
  selector: 'app-update-profile',
  templateUrl: './update-profile.component.html',
  styleUrls: ['./update-profile.component.css'],
  standalone: true,
  imports: [CommonModule, FormsModule],
  providers: []
})
export class UpdateProfileComponent implements OnInit {
  @ViewChild('fileInput') fileInput!: ElementRef;
  
  editInfoText = "Edit";
  editEmailText = "Edit";
  editMobileText = "Edit";
  saveInfoButton = false;
  saveEmailButton = false;
  saveMobileButton = false;
  hideButton = false;
  profileImageUrl = '../../../../assets/images/avatar1.png';
  
  userProfile: UserProfile = {
    firstName: 'Pranay',
    lastName: 'Raut',
    email: 'pranay.raut@example.com',
    mobileNumber: '555-123-4567',
    emailNotifications: true,
    smsNotifications: false
  };
  
  constructor(private notificationBus: NotificationBusService) {}

  ngOnInit(): void {
    // Load user profile data from API
    this.loadUserProfile();
  }

  private loadUserProfile(): void {
    // TODO: Replace with actual API call
    // For now using mock data
  }

  // Validation functions
  private validateEmail(email: string): boolean {
    const emailRegex = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}$/;
    return emailRegex.test(email);
  }

  private validateMobile(mobile: string): boolean {
    const mobileRegex = /^\+?[\d\s-]{10,}$/;
    return mobileRegex.test(mobile);
  }

  triggerFileInput(): void {
    this.fileInput.nativeElement.click();
  }

  onFileSelected(event: any): void {
    const file = event.target.files[0];
    if (file) {
      // Check if file is an image
      if (file.type.match(/image\/*/) == null) {
        this.notificationBus.error('Only image files are supported', 'Invalid File Type');
        return;
      }
      
      // Create file reader to read the file as data URL
      const reader = new FileReader();
      reader.readAsDataURL(file);
      
      reader.onload = (_event) => {
        this.profileImageUrl = reader.result as string;
      };
    }
  }

  personalInfoEdit() {
    if (this.editInfoText === "Edit") {
      this.editInfoText = "Cancel";
      this.saveInfoButton = true;
    } else {
      this.editInfoText = "Edit";
      this.saveInfoButton = false;
    }
  }
  
  emailEdit() {
    if (this.editEmailText === "Edit") {
      this.editEmailText = "Cancel";
      this.saveEmailButton = true;
    } else {
      this.editEmailText = "Edit";
      this.saveEmailButton = false;
    }
  }
  
  mobileNumberEdit() {
    if (this.editMobileText === "Edit") {
      this.editMobileText = "Cancel";
      this.saveMobileButton = true;
    } else {
      this.editMobileText = "Edit";
      this.saveMobileButton = false;
    }
  }
  
  savePersonalInfo(): void {
    if (!this.userProfile.firstName || !this.userProfile.lastName) {
      this.notificationBus.error('Please fill in all required fields', 'Validation Error');
      return;
    }

    // Here we would call the API to save personal info
    console.log('Saving personal info:', this.userProfile.firstName, this.userProfile.lastName);
    this.editInfoText = "Edit";
    this.saveInfoButton = false;
    this.notificationBus.success('Personal information updated successfully', 'Success');
  }
  
  saveEmail(): void {
    if (!this.validateEmail(this.userProfile.email)) {
      this.notificationBus.error('Please enter a valid email address', 'Validation Error');
      return;
    }

    // Here we would call the API to save email
    console.log('Saving email:', this.userProfile.email);
    this.editEmailText = "Edit";
    this.saveEmailButton = false;
    this.notificationBus.success('Email updated successfully', 'Success');
  }
  
  saveMobile(): void {
    if (!this.validateMobile(this.userProfile.mobileNumber)) {
      this.notificationBus.error('Please enter a valid mobile number', 'Validation Error');
      return;
    }

    // Here we would call the API to save mobile number
    console.log('Saving mobile number:', this.userProfile.mobileNumber);
    this.editMobileText = "Edit";
    this.saveMobileButton = false;
    this.notificationBus.success('Mobile number updated successfully', 'Success');
  }
}
