import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { CartProduct } from "../../models/cart.product.model";
import { Order } from "../../models/Order.model";
import { environment } from "src/environments/environment";


@Injectable({"providedIn":"root"})
export class OrderRestService{
    constructor(private rest: HttpClient){}

    placeOrder(products: CartProduct[]) : Observable<Order[]>{
        return this.rest.post<Order[]>(environment.baseURL+'order',products);
    }

    getOrders() : Observable<Order[]>{
        return this.rest.get<Order[]>(environment.baseURL+'order');
    }

}