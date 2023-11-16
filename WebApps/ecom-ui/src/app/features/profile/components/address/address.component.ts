import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-address',
  templateUrl: './address.component.html',
  styleUrls: ['./address.component.css']
})
export class AddressComponent implements OnInit {

  constructor(private route: Router) { }

  ngOnInit(): void {
  }

  btnText = "Add A New Address"
  addAddress() {
    if (this.btnText == "Add A New Address") {
      this.btnText = "Back"
      this.route.navigate(['user/profile/address/create']);
    } else {
      this.btnText = "Add A New Address"
      this.route.navigate(['user/profile/address/list']);
    }
  }
}
