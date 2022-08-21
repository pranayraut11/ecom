import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { Cart } from "../../models/cart";
import { CartProduct } from "../../models/cart.product.model";


@Injectable({providedIn:"root"})
export class CartRestService{

    constructor(private rest: HttpClient){}

    addToCart(cartProduct : CartProduct) : Observable<Cart>{
       return this.rest.post<Cart>("http://localhost:8081/cart/product",cartProduct);
    }

    getCartProducts() : Observable<Cart>{
        return this.rest.get<Cart>("http://localhost:8081/cart/product",{responseType:'json'});
    }

    removeFromCart(id: string){
       return this.rest.delete("http://localhost:8081/cart//product"+id);
    }

    updateProduct(cartProduct : CartProduct): Observable<Cart>{
        return this.rest.patch<Cart>("http://localhost:8081/cart/product",cartProduct);
    }
 
}