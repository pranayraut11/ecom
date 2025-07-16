import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { Address } from 'src/app/shared/models/address.model';
import { AddressRestService } from 'src/app/shared/services/rest-services/address-rest-service';

@Component({
  selector: 'app-list-address',
  templateUrl: './list-address.component.html',
  styleUrls: ['./list-address.component.css'],
  standalone: true,
  imports: [CommonModule]
})
export class ListAddressComponent implements OnInit {

  constructor(private addressRest: AddressRestService,private route : Router) { }

  addresses: Address[];
  ngOnInit(): void {
    this.getAllAddresses();
  }


  getAllAddresses() {
    this.addressRest.getAllAddress().subscribe((response) => {
      console.log(response);
      this.addresses = response;
    });
  }

  deleteAddress(id : string){
    this.addressRest.deleteAddress(id).subscribe((response)=>{
        console.log("Address deleted successfully"+response)
    });
  }

  getAddress(id : string){
    this.addressRest.getAddress(id).subscribe((response)=>{
      console.log(response);
    });
  }

  updateAddress(id : string){
    this.route.navigate(['user/profile/address/update/'+id]);
  }
}
