import { Component, OnInit } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from 'src/app/core/auth/services/auth.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-address',
  templateUrl: './address.component.html',
  styleUrls: ['./address.component.css'],
  standalone: true,
  imports: [
    RouterModule,
    CommonModule
  ]
})
export class AddressComponent implements OnInit {

  constructor(private route: Router, private authService: AuthService) { }
  roles;
  btnText = "Add A New Address"
  
  ngOnInit(): void {
    console.log(this.btnText)
    if(this.route.url.includes("create")){
      this.btnText = 'Back';
    }
    this.roles =  this.authService.user.subscribe();
    this.authService.user.subscribe(response => {
        if (response) {
          this.roles = response.roles;
        }
      });
  }

  addAddress() {
    let pathToNavigate = this.roles[0]+'/profile/address/';
    if (this.btnText == "Add A New Address") {
      this.btnText = "Back"
      this.route.navigate([pathToNavigate+'create']);
    } else {
      this.btnText = "Add A New Address"
      this.route.navigate([pathToNavigate+'list']);
    }
  }
}
