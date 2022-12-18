import { Component, OnInit } from '@angular/core';

import { Cart } from '../../../../shared/models/cart';
import { CartProduct } from '../../../../shared/models/cart.product.model';
import { OrderRestService } from '../../../../shared/services/rest-services/order-rest-service';
import { CartRestService } from '../../../../shared/services/rest-services/cart-rest-service';

@Component({
  selector: 'cart-list',
  templateUrl: './list.component.html',
  styleUrls: ['./list.component.css']
})
export class CartListComponent implements OnInit {

  constructor(private cartRestService: CartRestService,private orderRestService: OrderRestService) { }
  cart: Cart;
  cartProducts: CartProduct[];
  ngOnInit(): void {
    this.cartRestService.getCartProducts().subscribe((cart: Cart) => {
      console.log(cart);
      this.cart = cart;
      this.cartProducts = cart.products;
    });
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

  placeOrder(cart : Cart){
    console.log("Placing order");
    
    this.orderRestService.placeOrder(cart.products).subscribe(response=>{
      console.log("order placed!")
      console.log(response);
    });
  }


}
