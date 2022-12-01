import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Product } from "src/app/shared/models/product.model";
import { Observable } from "rxjs";

@Injectable({"providedIn":"root"})
export class ProductRestService{
 
    constructor(private rest : HttpClient ){}
    
    getProductList(): Observable<Product[]>{
      return  this.rest.get<Product[]>("http://localhost:30001/catalog/product",{responseType: 'json'});
    }

    createProduct(product : Product): Observable<Product>{
      return this.rest.post<Product>("http://localhost:30001/catalog/product",product);
    }

}