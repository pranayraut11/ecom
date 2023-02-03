import { Component, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';

@Component({
  selector: 'app-create-address',
  templateUrl: './create-address.component.html',
  styleUrls: ['./create-address.component.css']
})
export class CreateAddressComponent implements OnInit {

  constructor() { }

  ngOnInit(): void {
  }

  saveAddress(form: NgForm){
    console.log(form.value.addressData);

  }

}
