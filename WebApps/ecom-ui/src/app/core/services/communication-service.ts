import { Injectable } from "@angular/core";
import { BehaviorSubject, Subject } from "rxjs";
import { CartProduct } from "src/app/shared/models/cart.product.model";

@Injectable({ providedIn: 'root'})
export class CommunicationService{
    cartProducts :  BehaviorSubject<CartProduct[]>;
    buyNow :  BehaviorSubject<string>;
    constructor(){
        this.cartProducts = new BehaviorSubject(null);
        this.buyNow = new BehaviorSubject(null);
    }
    
    addCartProducts(data : CartProduct[]){
        this.cartProducts.next(data);
    }

    addProductId(productId : string){
        this.buyNow.next(productId);
    }
}