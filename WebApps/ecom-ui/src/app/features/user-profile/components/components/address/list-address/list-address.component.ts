import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule, ActivatedRoute } from '@angular/router';
import { Address } from 'src/app/shared/models/address.model';
import { AddressRestService } from 'src/app/shared/services/rest-services/address-rest-service';

@Component({
  selector: 'app-list-address',
  templateUrl: './list-address.component.html',
  styleUrls: ['./list-address.component.css'],
  standalone: true,
  imports: [CommonModule, RouterModule]
})
export class ListAddressComponent implements OnInit {

  constructor(
    private addressRest: AddressRestService, 
    public router: Router,  // Changed to public so template can access it
    private route: ActivatedRoute
  ) { }

  addresses: Address[];
  isCheckout: boolean = false;
  pageTitle: string = "My Addresses";
  
  ngOnInit(): void {
    this.getAllAddresses();
    
    // Check if this component is being used in checkout flow
    // First check the route data for direct navigation
    this.route.data.subscribe(data => {
      if (data && data['isCheckout']) {
        this.isCheckout = true;
        this.pageTitle = "Select a Delivery Address";
      }
    });
    
    // Also check URL in case it's navigated from shopping cart
    if (this.router.url.includes('checkout-address')) {
      this.isCheckout = true;
      this.pageTitle = "Select a Delivery Address";
    }
  }

  getAllAddresses() {
    this.addressRest.getAllAddress().subscribe((response) => {
      console.log(response);
      this.addresses = response;
    });
  }

  deleteAddress(id: string) {
    this.addressRest.deleteAddress(id).subscribe((response) => {
        console.log("Address deleted successfully"+response);
        this.getAllAddresses(); // Refresh the list after delete
    });
  }

  getAddress(id: string) {
    this.addressRest.getAddress(id).subscribe((response) => {
      console.log(response);
    });
  }

  updateAddress(id: string) {
    // Stop event propagation to prevent triggering address selection in checkout mode
    this.router.navigate(['user/profile/address/update', id]);
  }
  
  selectAddressAndProceed(addressId: string) {
    // Here you would typically save the selected address ID to a service or localStorage
    console.log('Selected address ID:', addressId);
    localStorage.setItem('selectedAddressId', addressId);
    
    // Navigate to payment page
    this.router.navigate(['/user/payment']);
  }
  
  proceedWithDefaultAddress() {
    // Find the default address or use the first one if no default is set
    const defaultAddress = this.addresses.find(a => a.defaultAddress);
    const addressToUse = defaultAddress ? defaultAddress : this.addresses[0];
    
    if (addressToUse) {
      this.selectAddressAndProceed(addressToUse.id);
    }
  }
}
