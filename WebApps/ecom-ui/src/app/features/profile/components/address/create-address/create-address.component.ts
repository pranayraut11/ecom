import { Component, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Address } from 'src/app/shared/models/address.model';
import { AddressRestService } from 'src/app/shared/services/rest-services/address-rest-service';

@Component({
  selector: 'app-create-address',
  templateUrl: './create-address.component.html',
  styleUrls: ['./create-address.component.css']
})
export class CreateAddressComponent implements OnInit {

  address = new Address("","","","","","","","","","","",false);
  initialized : boolean=false;
  constructor(private addressRest: AddressRestService, private actRoute: ActivatedRoute) { }
  btnText:string = 'Save'
  ngOnInit(): void {
    let id = this.actRoute.snapshot.paramMap.get("id");
    if(id){
      this.getAddress(id);
    }
  }

  saveAddress(form: NgForm) {
    console.log(form.value.addressData);
    const add = form.value.addressData;
    let address = new Address(null, add.first_name, add.last_name, add.addressLine1, add.addressLine2, add.landmark, add.city, add.state, add.pincode,add.mobile,add.type,add.defaultAddress);
    this.addressRest.addOrUpdateAddress(address).subscribe((response) => {
      console.log("Address saved successfully")
    })
  }

  getAddress(id: string) {
    this.addressRest.getAddress(id).subscribe((response) => {
      this.address = response;
      console.log(this.address);
      this.btnText = 'Update';
    })
  }
}
