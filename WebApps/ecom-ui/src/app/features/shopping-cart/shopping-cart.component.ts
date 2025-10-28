import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { AuthService } from 'src/app/core/auth/services/auth.service';
import { Cart } from 'src/app/shared/models/cart';
import { CartProduct } from 'src/app/shared/models/cart.product.model';
import { CartRestService } from 'src/app/shared/services/rest-services/cart-rest-service';
import { CommonModule } from '@angular/common';
import { TokenUtil } from 'src/app/utils/TokenUtil';

@Component({
  selector: 'shopping-cart',
  templateUrl: './shopping-cart.component.html',
  styleUrls: ['./shopping-cart.component.css'],
  standalone: true,
  imports: [
    RouterModule,
    CommonModule
  ]
})
export class ShoppingCartComponent implements OnInit {
  cart: Cart;
  emptyCart = false;
  isAuthenticated = false;
  btnText = "Proceed to Buy";

  constructor(
    private cartRestService: CartRestService, 
    private authService: AuthService,
    private route: ActivatedRoute, 
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadCartData();
    
    if(this.router.url.includes("address")){
      this.btnText = "Use this Address";
    } else if(this.router.url.includes("payment")){
      this.btnText = "Place Order";
    }
    
    this.isAuthenticated = TokenUtil.isTokenValid();
  }

  loadCartData(): void {
    this.cartRestService.getMyCartProducts().subscribe({
      next: (products: CartProduct[]) => {
        if (products && products.length > 0) {
          // For now, create a simplified cart structure
          this.cart = {
            id: '1',
            total: this.calculateTotal(products),
            totalPrice: this.calculateTotalPrice(products),
            discount: this.calculateDiscount(products),
            products: products,
            deliveryCharges: this.calculateDeliveryCharges(products)
          } as Cart;
          this.emptyCart = false;
        } else {
          this.emptyCart = true;
        }
      },
      error: (error) => {
        console.error('Error loading cart:', error);
        this.emptyCart = true;
      }
    });
  }

  calculateTotal(products: CartProduct[]): number {
    return products.reduce((sum, product) => {
      // Use the price (already discounted) as the base price
      return sum + (product.price * product.quantity);
    }, 0);
  }

  calculateTotalPrice(products: CartProduct[]): number {
    return products.reduce((sum, product) => {
      // Use originalPrice if available, otherwise use price
      const basePrice = product.originalPrice || product.price;
      return sum + (basePrice * product.quantity);
    }, 0);
  }

  calculateDiscount(products: CartProduct[]): number {
    return products.reduce((sum, product) => {
      // If originalPrice exists and is greater than price, calculate the discount
      if (product.originalPrice && product.originalPrice > product.price) {
        return sum + ((product.originalPrice - product.price) * product.quantity);
      }
      return sum;
    }, 0);
  }

  calculateDeliveryCharges(products: CartProduct[]): number {
    return products.reduce((sum, product) => sum + (product.deliveryCharge || 0), 0);
  }

  proceedToBuy(): void {
    if (this.isAuthenticated) {
      this.router.navigate(['/user/checkout-address']);
    } else {
      this.router.navigate(['/user/login']);
    }
  }
}
