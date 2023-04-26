import { HttpClient } from "@angular/common/http";
import { EnvironmentInjector, Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { Cart } from "../../models/cart";
import { CartProduct } from "../../models/cart.product.model";
import { environment } from "src/environments/environment";
import { CART_PRODUCT } from "../../constants/ApiEndpoints";


@Injectable({providedIn:"root"})
export class CartRestService{

    constructor(private rest: HttpClient){}

    addToCart(cartProduct : CartProduct) : Observable<Cart>{
       return this.rest.post<Cart>(environment.baseURL+CART_PRODUCT,cartProduct);
    }

    getCartProducts() : Observable<Cart>{
        return this.rest.get<Cart>(environment.baseURL+CART_PRODUCT,{responseType:'json'});
    }

    removeFromCart(id: string){
       return this.rest.delete(environment.baseURL+CART_PRODUCT+id);
    }

    updateProduct(cartProduct : CartProduct): Observable<Cart>{
        return this.rest.patch<Cart>(environment.baseURL+CART_PRODUCT,cartProduct);
    }
 
}