import { Component, Input, OnInit } from '@angular/core';
import { Cart } from '../shared/models/cart';
import { CartProduct } from '../shared/models/cart.product.model';
import { CartRestService } from '../shared/services/rest-services/cart-rest-service';

@Component({
  selector: 'app-cart',
  templateUrl: './cart.component.html',
  styleUrls: ['./cart.component.css']
})
export class CartComponent implements OnInit {

  constructor(private cartRestService: CartRestService) { }
  cart: Cart;
  cartProducts: CartProduct[];
  @Input() quantity: number;
  ngOnInit(): void {
    this.cartRestService.getCartProducts().subscribe((cart: Cart) => {
      console.log(cart);
      this.cartProducts = cart.products;
    });
  }

  increaseQuantity(cartProduct: CartProduct) {
    let product: CartProduct;
    product = this.cartProducts.find(product => product.productId === cartProduct.productId);
    product.quantity = product.quantity + 1;
  }

  decreaseQuantity(cartProduct: CartProduct) {
    let product: CartProduct;
    product = this.cartProducts.find(cartProducts => cartProducts.productId === cartProduct.productId);
    product.quantity = product.quantity - 1;
  }

  removeFromCart(cartProduct: CartProduct) {
    console.log(cartProduct.productId);
    this.cartRestService.removeFromCart(cartProduct.productId).subscribe(response => {
      console.log(response);
    });
  }


}
