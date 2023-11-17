import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CartListComponent } from './pages/list/list.component';
import { Cart } from 'src/app/shared/models/cart';
import { CartRestService } from 'src/app/shared/services/rest-services/cart-rest-service';
import { CartProduct } from 'src/app/shared/models/cart.product.model';

@Component({
  selector: 'cart-app',
  templateUrl: './cart.component.html',
  styleUrls: ['./cart.component.css']
})
export class CartComponent implements OnInit {

  cart: Cart;
  cartProducts: CartProduct[];
  btnText = "Proceed to Buy";
  constructor(private route: Router,private cartRestService: CartRestService) { }
  ngOnInit(): void {

    console.log("In cart service");
    //this.reload.emit('proc');
    this.cartRestService.getCartProducts().subscribe((cart: Cart) => {
      
      if (cart) {
        console.log("In cart condition");
        this.cart = cart;
        this.cartProducts = cart.products;
        console.log(this.cart);
        console.log(this.cartProducts);
      }
    });
    
  }


  onOutletLoaded(component: CartListComponent) {
    console.log(component)
    if (component instanceof CartListComponent) {
      console.log("Yes")
      this.btnText = "Proceed to Buy";
    }
  }

  proceedToBuy() {
    if (this.btnText == "Proceed to Buy") {
      this.btnText = "Use this Address";
      this.route.navigate(["user/cart/address"])
    } else if (this.btnText == "Use this Address") {
      this.btnText = "Place order";
      this.route.navigate(["user/cart/payment/savedcards"])
    } else if(this.btnText == "Place order"){
      this.route.navigate(["user/order-successfull"])
    }
  }
}
