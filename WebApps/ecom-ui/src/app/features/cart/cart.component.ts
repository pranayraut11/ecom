import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from 'src/app/core/core/auth/Auth-Service';
import { Cart } from 'src/app/shared/models/cart';
import { CartProduct } from 'src/app/shared/models/cart.product.model';
import { CartRestService } from 'src/app/shared/services/rest-services/cart-rest-service';
import { CartListComponent } from './pages/list/list.component';
import { CommunicationService } from 'src/app/core/services/communication-service';

@Component({
  selector: 'cart-app',
  templateUrl: './cart.component.html',
  styleUrls: ['./cart.component.css']
})
export class CartComponent implements OnInit {


  constructor(private route: Router, private cartRestService: CartRestService, private authService: AuthService, private communicationService: CommunicationService) { }

  cart: Cart;
  emptyCart: boolean = true;
  cartProducts: CartProduct[];
  btnText = "Proceed to Buy";
  isAuthenticated: boolean;

  ngOnInit(): void {

    console.log("In cart service");
    if (this.authService.isTokenExpired()) {
      console.log("In Cart token is expired")
      var productIds= new Array();
      productIds.push("1")
      this.cartRestService.getCartProducts(productIds).subscribe((products: CartProduct[]) => {
        if (products) {
          this.communicationService.addData(products);
          this.calculateTotal(products)
          this.emptyCart = false;
        } else {
          this.emptyCart = true;
        }
      });
      this.isAuthenticated=false;
    } else {
      console.log("In Cart token is not expired")
      this.cartRestService.getMyCartProducts().subscribe((products: CartProduct[]) => {
        if (products) {
          this.communicationService.addData(products);
          this.calculateTotal(products)
          this.emptyCart = false;
        } else {
          this.emptyCart = true;
        }
      });
      this.isAuthenticated=true;
    }
  }

  calculateTotal(products: CartProduct[]) {
    var total = 0;
    var totalSaving = 0;
    var deliveryCharge = 0;
    products.forEach(product => {
      total += product.discountedPrice * 1;
      totalSaving += product.price * 1 - product.discountedPrice * 1;
      deliveryCharge += product.deliveryCharge;
    })
    this.cart = new Cart("1", total, total + deliveryCharge, totalSaving, products, deliveryCharge);

  }

  onOutletLoaded(component: any) {
    console.log("Child data .. ")
    if (component instanceof CartListComponent) {
      console.log("Component cart " + component.cart)
      this.cart = component.cart;
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
    } else if (this.btnText == "Place order") {
      this.route.navigate(["user/order-successfull"])
    }
  }
}
