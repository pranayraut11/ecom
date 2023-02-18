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

  btnText = "Add Address"
  addAddress() {
    if (this.btnText == "Add Address") {
      this.btnText = "Back"
      this.route.navigate(['user/profile/address/create']);
    } else {
      this.btnText = "Add Address"
      this.route.navigate(['user/profile/address/list']);
    }
  }
}
