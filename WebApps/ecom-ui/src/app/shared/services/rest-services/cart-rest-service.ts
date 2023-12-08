import { HttpClient, HttpHeaders } from "@angular/common/http";
import { EnvironmentInjector, Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { Cart } from "../../models/cart";
import { CartProduct } from "../../models/cart.product.model";
import { environment } from "src/environments/environment";
import { CART, CART_SERVICE, PRODUCT } from "../../constants/ApiEndpoints";


@Injectable({providedIn:"root"})
export class CartRestService{

    constructor(private rest: HttpClient){}

    addToCart(cartProduct : CartProduct) : Observable<Cart>{
      const headers = new HttpHeaders()
        .set("X-CustomHeader", "none");
       return this.rest.post<Cart>(environment.baseURL+CART_SERVICE+CART,cartProduct,{headers});
    }

    getMyCartProducts() : Observable<CartProduct[]>{
        return this.rest.get<CartProduct[]>(environment.baseURL+CART_SERVICE+CART,{responseType:'json'});
    }

    getCartProducts(productIds: string[]) : Observable<CartProduct[]>{
        const headers = new HttpHeaders()
        .set("X-CustomHeader", "none");
        return this.rest.post<CartProduct[]>(environment.baseURL+CART_SERVICE+CART+"/product-ids",productIds,{headers,responseType:'json'});
    }

    removeFromCart(id: string){
       return this.rest.delete(environment.baseURL+CART_SERVICE+CART+'/'+id);
    }

    updateProduct(cartProduct : CartProduct): Observable<Cart>{
        return this.rest.patch<Cart>(environment.baseURL+CART_SERVICE+CART,cartProduct);
    }
 
}