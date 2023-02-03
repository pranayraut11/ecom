import { HttpClient } from "@angular/common/http";
import { EnvironmentInjector, Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { Cart } from "../../models/cart";
import { CartProduct } from "../../models/cart.product.model";
import { environment } from "src/environments/environment";


@Injectable({providedIn:"root"})
export class CartRestService{

    constructor(private rest: HttpClient){}

    addToCart(cartProduct : CartProduct) : Observable<Cart>{
       return this.rest.post<Cart>(environment.localURL+'cart/product',cartProduct);
    }

    getCartProducts() : Observable<Cart>{
        return this.rest.get<Cart>(environment.localURL+'cart/product',{responseType:'json'});
    }

    removeFromCart(id: string){
       return this.rest.delete(environment.localURL+'cart/product/'+id);
    }

    updateProduct(cartProduct : CartProduct): Observable<Cart>{
        return this.rest.patch<Cart>(environment.localURL+'cart/product',cartProduct);
    }
 
}