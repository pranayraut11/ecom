import { HttpClient, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { CartProduct } from "../../models/cart.product.model";
import { Order } from "../../models/Order.model";
import { environment } from "src/environments/environment";
import { CreateOrder } from "../../models/CreateOrder.model";


@Injectable({"providedIn":"root"})
export class OrderRestService{
    constructor(private rest: HttpClient){}
    placeOrder(order: CreateOrder) : Observable<Order[]>{
        return this.rest.post<Order[]>(environment.baseURL+'orders',order);
    }

    getOrders() : Observable<Order[]>{
        return this.rest.get<Order[]>(environment.baseURL+'orders');
    }

}