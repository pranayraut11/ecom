import { Component, OnInit } from '@angular/core';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ProfileMenuComponent } from './components/profile-menu/profile-menu.component';

@Component({
  selector: 'app-user-profile',
  templateUrl: './user-profile.component.html',
  styleUrls: ['./user-profile.component.css'],
  standalone: true,
  imports: [
    RouterModule,
    CommonModule,
    ProfileMenuComponent
  ]
})
export class UserProfileComponent implements OnInit {
  constructor() { }

  ngOnInit(): void {
  }
}
