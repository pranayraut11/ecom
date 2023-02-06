import { Component, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { Address } from 'src/app/shared/models/address.model';
import { UserRestService } from 'src/app/shared/services/rest-services/user-rest-service';

@Component({
  selector: 'app-create-address',
  templateUrl: './create-address.component.html',
  styleUrls: ['./create-address.component.css']
})
export class CreateAddressComponent implements OnInit {

  constructor(private userRest: UserRestService) { }

  ngOnInit(): void {
  }

  saveAddress(form: NgForm) {
    console.log(form.value.addressData);
    const add = form.value.addressData;
    let address = new Address(null,add.first_name,add.last_name,add.addressLine1,add.addressLine2,add.landmark,add.city,add.state,add.pincode);
    this.userRest.addOrUpdateAddress(address).subscribe((response)=>{
      console.log("Address saved successfully")
    })
  }

}
