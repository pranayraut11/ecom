import { Injectable } from "@angular/core";
import { CART } from "src/app/shared/constants/shared-constant";
import { CartProduct } from "src/app/shared/models/cart.product.model";
import { Product } from "src/app/shared/models/product.model";
import { CartRestService } from "src/app/shared/services/rest-services/cart-rest-service";

@Injectable({providedIn:"root"})
export class CartService {

    constructor(private cartRestService : CartRestService){}
    addProductToCart(productId:string){
      let cart =  localStorage.getItem(CART);
        if(cart){
            let ids = new Array();
            ids = JSON.parse(cart);
            if(!ids.includes(productId)){
                ids.push(productId);
                localStorage.setItem(CART,JSON.stringify(ids));
            }
        }else{
            let ids = new Array();
            ids.push(productId);
            localStorage.setItem(CART,JSON.stringify(ids));
        }
    }

    addToCart(product: Product) {
        let cartProduct = new CartProduct("", product.id, product.name, product.images[0], product.price.price, product.price.discountedPrice, product.price.discount, 1,150);
        console.log(product);
        this.addProductToCart(product.id);
        this.cartRestService.addToCart(cartProduct).subscribe(response => {
          console.log(response);
        });;
      }
}