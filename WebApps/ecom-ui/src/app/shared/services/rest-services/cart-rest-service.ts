import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { Cart } from "../../models/cart";
import { CartProduct } from "../../models/cart.product.model";


@Injectable({providedIn:"root"})
export class CartRestService{

    constructor(private rest: HttpClient){}

    addToCart(cartProduct : CartProduct){
        this.rest.post("http://localhost:8081/cart/products",cartProduct).subscribe(response=>{
            console.log(response);
        });
    }

    getCartProducts() : Observable<Cart>{
        return this.rest.get<Cart>("http://localhost:8081/cart/products",{responseType:'json'});
    }

    removeFromCart(id: string){
       return this.rest.delete("http://localhost:8081/cart/"+id);
    }
}