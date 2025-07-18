import { Component, OnInit } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { RolesDirective } from 'src/app/core/directives/roles.directive';

@Component({
  selector: 'app-profile-menu',
  templateUrl: './profile-menu.component.html',
  styleUrls: ['./profile-menu.component.css'],
  standalone: true,
  imports: [
    RouterModule,
    CommonModule,
    RolesDirective
  ]
})
export class ProfileMenuComponent implements OnInit {

  constructor(private route:Router) { }

  ngOnInit(): void {
  }

  redirectToMyOrders(){
    this.route.navigate(["user/profile/myorders"]);
  }

}
