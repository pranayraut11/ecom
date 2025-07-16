import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { SharedModule } from '../../shared/shared.module';

// Component imports
import { ProfileComponent } from './profile.component';
import { ProfileMenuComponent } from './components/profile-menu/profile-menu.component';
import { AddressComponent } from './components/address/address.component';

import { CreateAddressComponent } from './components/address/create-address/create-address.component';
import { UpdateProfileComponent } from './components/update-profile/update-profile.component';
import { NotificationComponent } from './components/notification/notification.component';

// Routing import
import { ProfileRoutingModule } from './profile-routing.module';

@NgModule({
  imports: [
    CommonModule,
    RouterModule,
    HttpClientModule,
    FormsModule,
    ReactiveFormsModule,
    SharedModule,
    ProfileRoutingModule,
    // Import standalone components
    ProfileComponent,
    ProfileMenuComponent,
    AddressComponent,

    CreateAddressComponent,
    UpdateProfileComponent,
    NotificationComponent
  ]
})
export class ProfileModule { }
