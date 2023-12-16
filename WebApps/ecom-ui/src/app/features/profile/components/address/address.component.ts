import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from 'src/app/core/core/auth/Auth-Service';

@Component({
  selector: 'app-address',
  templateUrl: './address.component.html',
  styleUrls: ['./address.component.css']
})
export class AddressComponent implements OnInit {

  constructor(private route: Router,private authService: AuthService) { }
  roles;
  ngOnInit(): void {
    this.roles =  this.authService.user.subscribe();
    this.authService.user.subscribe(response => {
        if (response) {
          this.roles = response.roles;
        }
      });
  }

  btnText = "Add A New Address"
  addAddress() {
    let pathToNavigate = this.roles[0]+'/profile/address/';
    if (this.btnText == "Add A New Address") {
      this.btnText = "Back"
      this.route.navigate([pathToNavigate+'/create']);
    } else {
      this.btnText = "Add A New Address"
      this.route.navigate([pathToNavigate+'/list']);
    }
  }

}
