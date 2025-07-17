import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Cart } from '../../../../shared/models/cart';
import { CartProduct } from '../../../../shared/models/cart.product.model';
import { OrderRestService } from '../../../../shared/services/rest-services/order-rest-service';
import { CartRestService } from '../../../../shared/services/rest-services/cart-rest-service';
import { CreateOrder } from 'src/app/shared/models/CreateOrder.model';
import { CommunicationService } from 'src/app/core/services/communication-service';

@Component({
  selector: 'cart-list',
  templateUrl: './list.component.html',
  styleUrls: ['./list.component.css'],
  standalone: true,
  imports: [CommonModule]
})
export class CartListComponent implements OnInit {
  constructor(private cartRestService: CartRestService, 
              private orderRestService: OrderRestService,
              private communicationService: CommunicationService) { }
              
  cart: Cart;
  cartProducts: CartProduct[];
  cartResponse: boolean;
  cartId: string;
  cartRequestDto: any;
  emptyCart: boolean = false;
  
  ngOnInit(): void {
    this.loadCartProducts();
  }

  loadCartProducts() {
    this.cartRestService.getMyCartProducts().subscribe(
      (response: any) => {
        console.log("Response " + response);
        if (response) {
          // Build cart from response products
          const products = response as CartProduct[];
          if (products && products.length > 0) {
            this.emptyCart = false;
            this.cartProducts = products;
            
            // Create cart object
            const total = this.calculateTotal(products);
            const totalPrice = this.calculateTotalPrice(products);
            const discount = totalPrice - total;
            const deliveryCharges = this.calculateDeliveryCharges(products);
            
            this.cart = {
              id: '1', // Default ID
              total: total,
              totalPrice: totalPrice,
              discount: discount,
              products: products,
              deliveryCharges: deliveryCharges
            };
          } else {
            this.emptyCart = true;
          }
        } else {
          this.emptyCart = true;
        }
      }
    );
  }

  calculateTotal(products: CartProduct[]): number {
    return products.reduce((sum, product) => sum + (product.price * product.quantity), 0);
  }

  calculateTotalPrice(products: CartProduct[]): number {
    return products.reduce((sum, product) => sum + ((product.originalPrice || product.price) * product.quantity), 0);
  }

  calculateDeliveryCharges(products: CartProduct[]): number {
    return products.reduce((sum, product) => sum + (product.deliveryCharge || 0), 0);
  }

  createOrder() {
    const createOrderRequest: CreateOrder = {
      buyNow: false,
      id: this.cart.id
    };
    
    // This is a placeholder since createOrder doesn't exist in OrderRestService
    console.log('Creating order with ID:', this.cart.id);
    // In a real implementation, you would call the service method
  }

  updateQuantity(itemId: string, quantity: number) {
    this.cartRequestDto = {
      "productId": itemId,
      "quantity": quantity
    };
    this.cartRestService.addToCart(this.cartRequestDto).subscribe((cartResponse) => {
      this.loadCartProducts();
      console.log("Reloaded");
    });
  }

  increaseQuantity(item: CartProduct) {
    this.updateQuantity(item.productId, 1);
  }

  decreaseQuantity(item: CartProduct) {
    if (item.quantity > 1) {
      this.updateQuantity(item.productId, -1);
    }
  }
}
