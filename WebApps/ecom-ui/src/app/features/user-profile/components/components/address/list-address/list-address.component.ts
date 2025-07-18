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
  selectedAddressId: string = null;
  
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
    
    // Retrieve previously selected address if any
    if (this.isCheckout) {
      const savedAddressId = localStorage.getItem('selectedAddressId');
      if (savedAddressId) {
        this.selectedAddressId = savedAddressId;
      }
    }
  }

  getAllAddresses() {
    this.addressRest.getAllAddress().subscribe({
      next: (response) => {
        console.log(response);
        this.addresses = response;
        
        // If no address is selected yet but we have addresses and we're in checkout mode
        if (this.isCheckout && !this.selectedAddressId && this.addresses && this.addresses.length > 0) {
          // Find default address or use first one
          const defaultAddress = this.addresses.find(a => a.defaultAddress);
          if (defaultAddress) {
            this.selectedAddressId = defaultAddress.id;
          } else {
            this.selectedAddressId = this.addresses[0].id;
          }
          // Save to localStorage
          localStorage.setItem('selectedAddressId', this.selectedAddressId);
        }
      },
      error: (err) => {
        console.error('Error fetching addresses:', err);
      }
    });
  }

  deleteAddress(id: string) {
    this.addressRest.deleteAddress(id).subscribe({
      next: (response) => {
        console.log("Address deleted successfully"+response);
        
        // If the deleted address was selected, clear the selection
        if (this.selectedAddressId === id) {
          this.selectedAddressId = null;
          localStorage.removeItem('selectedAddressId');
        }
        
        this.getAllAddresses(); // Refresh the list after delete
      },
      error: (err) => {
        console.error('Error deleting address:', err);
      }
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
    // Update selected address in component
    this.selectedAddressId = addressId;
    
    // Save selected address ID to localStorage
    localStorage.setItem('selectedAddressId', addressId);
    
    if (this.isCheckout) {
      // Navigate to payment page
      this.router.navigate(['/user/payment']);
    }
  }
  
  selectAddress(addressId: string) {
    // Just update selected address in component and localStorage
    this.selectedAddressId = addressId;
    localStorage.setItem('selectedAddressId', addressId);
  }
  
  proceedWithDefaultAddress() {
    // Find the default address or use the first one if no default is set
    const defaultAddress = this.addresses.find(a => a.defaultAddress);
    const addressToUse = defaultAddress ? defaultAddress : this.addresses[0];
    
    if (addressToUse) {
      this.selectAddressAndProceed(addressToUse.id);
    }
  }
  
  isAddressSelected(addressId: string): boolean {
    return this.selectedAddressId === addressId;
  }
}
