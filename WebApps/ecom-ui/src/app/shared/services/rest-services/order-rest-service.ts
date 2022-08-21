import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { CartProduct } from "../../models/cart.product.model";
import { Order } from "../../models/Order.model";


@Injectable({"providedIn":"root"})
export class OrderRestService{
    constructor(private rest: HttpClient){}

    placeOrder(products: CartProduct[]) : Observable<Order[]>{
        return this.rest.post<Order[]>("http://localhost:8082/order",products);
    }

    getOrders() : Observable<Order[]>{
        return this.rest.get<Order[]>("http://localhost:8082/order");
    }

}