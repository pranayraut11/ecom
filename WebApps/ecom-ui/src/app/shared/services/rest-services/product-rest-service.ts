import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Product } from "src/app/shared/models/product.model";
import { Observable } from "rxjs";
import { environment } from "src/environments/environment";

@Injectable({"providedIn":"root"})
export class ProductRestService{
 
    constructor(private rest : HttpClient ){}
    
    getProductList(): Observable<Product[]>{
      return  this.rest.get<Product[]>(environment.baseURL+'catalog/product',{responseType: 'json'});
    }

    createProduct(product : Product): Observable<Product>{
      return this.rest.post<Product>(environment.baseURL+'catalog/product',product);
    }

}