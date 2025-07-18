import { Component, OnInit } from '@angular/core';
import { NgForm, FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { Address } from 'src/app/shared/models/address.model';
import { AddressRestService } from 'src/app/shared/services/rest-services/address-rest-service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-create-address',
  templateUrl: './create-address.component.html',
  styleUrls: ['./create-address.component.css'],
  standalone: true,
  imports: [FormsModule, CommonModule, RouterModule]
})
export class CreateAddressComponent implements OnInit {

  address = new Address("","","","","","","","","","","",false);
  initialized: boolean = false;
  btnText: string = 'Save';
  isCheckout: boolean = false;
  pageTitle: string = "Add New Address";
  formSubmitted: boolean = false;
  
  constructor(
    private addressRest: AddressRestService, 
    private route: ActivatedRoute,
    public router: Router
  ) { }
  
  ngOnInit(): void {
    let id = this.route.snapshot.paramMap.get("id");
    if(id){
      this.getAddress(id);
      this.pageTitle = "Update Address";
      this.btnText = 'Update';
    }
    
    // Check if this component is being used in checkout flow
    this.route.data.subscribe(data => {
      if (data && data['isCheckout']) {
        this.isCheckout = true;
      }
    });
    
    // Also check URL
    if (this.router.url.includes('checkout-add-address')) {
      this.isCheckout = true;
    }
  }

  saveAddress(form: NgForm) {
    this.formSubmitted = true;
    
    if (form.invalid) {
      // Scroll to the first invalid field
      const firstInvalidElement = document.querySelector('.ng-invalid');
      if (firstInvalidElement) {
        firstInvalidElement.scrollIntoView({ behavior: 'smooth', block: 'center' });
      }
      return;
    }
    
    console.log(form.value.addressData);
    const add = form.value.addressData;
    let address = new Address(null, add.firstName, add.lastName, add.addressLine1, add.addressLine2, add.landmark, add.city, add.state, add.pincode, add.mobile, add.type, add.defaultAddress);

    // If this is the first address being added, make it default
    if (!this.initialized) {
      this.addressRest.getAllAddress().subscribe(addresses => {
        if (!addresses || addresses.length === 0) {
          address.defaultAddress = true;
        }
        this.saveAddressToServer(address);
      });
    } else {
      this.saveAddressToServer(address);
    }
  }

  private saveAddressToServer(address: Address) {
    this.addressRest.addOrUpdateAddress(address).subscribe({
      next: (response) => {
        console.log("Address saved successfully");
        
        // Set initialized flag after first address is added
        if (!this.initialized) {
          this.initialized = true;
        }
        
        // Navigate based on mode (checkout or normal flow)
        if (this.isCheckout) {
          // If in checkout flow, navigate to payment
          this.router.navigate(['/user/payment']);
        } else {
          // In normal flow, go back to address list
          this.router.navigate(['/user/profile/address']);
        }
      },
      error: (err) => {
        console.error("Error saving address:", err);
        // Handle error - could add toast notification here
      }
    });
  }

  getAddress(id: string) {
    this.addressRest.getAddress(id).subscribe({
      next: (response) => {
        this.address = response;
        console.log(this.address);
      },
      error: (err) => {
        console.error("Error fetching address:", err);
        // Handle error
      }
    });
  }
  
  // Cancel and go back
  cancelAndGoBack() {
    if (this.isCheckout) {
      this.router.navigate(['/user/checkout-address']);
    } else {
      this.router.navigate(['/user/profile/address']);
    }
  }
}
