import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';

import { Cart } from '../../../../shared/models/cart';
import { CartProduct } from '../../../../shared/models/cart.product.model';
import { OrderRestService } from '../../../../shared/services/rest-services/order-rest-service';
import { CartRestService } from '../../../../shared/services/rest-services/cart-rest-service';
import { CreateOrder } from 'src/app/shared/models/CreateOrder.model';
import { CommunicationService } from 'src/app/core/services/communication-service';

@Component({
  selector: 'cart-list',
  templateUrl: './list.component.html',
  styleUrls: ['./list.component.css']
})
export class CartListComponent implements OnInit {
 // @Output() reload = new EventEmitter<any>();
  constructor(private cartRestService: CartRestService, private orderRestService: OrderRestService,private communicationService : CommunicationService) { }
  cart: Cart;
  cartProducts: CartProduct[];
  initialized = false;
  ngOnInit(): void {
    console.log("In cart list service");
    this.communicationService.cartProducts.subscribe(
      res=>    this.cartProducts = res
    );
    
  }

  increaseQuantity(cartProduct: CartProduct) {
    let product: CartProduct;
    product = this.cartProducts.find(product => product.productId === cartProduct.productId);
    product.quantity = product.quantity + 1;

    this.cartRestService.updateProduct(product).subscribe(response => {
      console.log(response);
      this.cart = response;
    });
  }

  decreaseQuantity(cartProduct: CartProduct) {
    let product: CartProduct;
    product = this.cartProducts.find(cartProducts => cartProducts.productId === cartProduct.productId);
    product.quantity = product.quantity - 1;
    this.cartRestService.updateProduct(product).subscribe(response => {
      console.log(response);
      this.cart = response;
    });
  }

  removeFromCart(cartProduct: CartProduct) {
    console.log(cartProduct.productId);
    this.cartRestService.removeFromCart(cartProduct.productId).subscribe(response => {
      console.log(response);
    });
  }

  placeOrder(cart: Cart) {
    console.log("Placing order");
    let order = new CreateOrder(false, cart.id);
    this.orderRestService.placeOrder(order).subscribe(response => {
      this.cartProducts = null;
      console.log("order placed!")
      console.log(response);
     
    });
  }


}
