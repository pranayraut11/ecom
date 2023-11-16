import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-profile-menu',
  templateUrl: './profile-menu.component.html',
  styleUrls: ['./profile-menu.component.css']
})
export class ProfileMenuComponent implements OnInit {

  constructor(private route:Router) { }

  ngOnInit(): void {
  }

  redirectToMyOrders(){
this.route.navigate(["user/myorders"]);
  }

}
