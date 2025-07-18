import { Component, OnInit } from '@angular/core';
import { RouterModule, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { Address } from 'src/app/shared/models/address.model';
import { AddressRestService } from 'src/app/shared/services/rest-services/address-rest-service';

@Component({
  selector: 'app-payment',
  templateUrl: './payment.component.html',
  styleUrls: ['./payment.component.css'],
  standalone: true,
  imports: [
    RouterModule,
    CommonModule
  ]
})
export class PaymentComponent implements OnInit {
  selectedAddress: Address = null;
  isLoading: boolean = true;
  
  constructor(
    private router: Router,
    private addressService: AddressRestService
  ) { }

  ngOnInit(): void {
    this.loadSelectedAddress();
  }
  
  loadSelectedAddress(): void {
    const selectedAddressId = localStorage.getItem('selectedAddressId');
    
    if (selectedAddressId) {
      this.addressService.getAddress(selectedAddressId).subscribe({
        next: (address) => {
          this.selectedAddress = address;
          this.isLoading = false;
        },
        error: (err) => {
          console.error('Error loading selected address:', err);
          this.isLoading = false;
          // Redirect to address selection if there's an error
          this.router.navigate(['/user/checkout-address']);
        }
      });
    } else {
      this.isLoading = false;
      // Redirect to address selection if no address ID is found
      this.router.navigate(['/user/checkout-address']);
    }
  }

  placeOrder() {
    // TODO: Add actual order placement logic here
    this.router.navigate(['/user/order-successfull']);
  }
  
  changeAddress() {
    this.router.navigate(['/user/checkout-address']);
  }
}
