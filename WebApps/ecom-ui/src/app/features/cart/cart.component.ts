import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CartListComponent } from './pages/list/list.component';

@Component({
  selector: 'cart-app',
  templateUrl: './cart.component.html',
  styleUrls: ['./cart.component.css']
})
export class CartComponent implements OnInit {

  btnText = "Proceed to Buy";
  constructor(private route: Router) { }
  ngOnInit(): void {

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
