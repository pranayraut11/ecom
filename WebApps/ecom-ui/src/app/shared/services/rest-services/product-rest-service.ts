import { Injectable } from "@angular/core";
import { HttpClient, HttpHeaders, HttpParams } from "@angular/common/http";
import { Product } from "src/app/shared/models/product.model";
import { Observable } from "rxjs";
import { environment } from "src/environments/environment";

@Injectable({ "providedIn": "root" })
export class ProductRestService {

  constructor(private rest: HttpClient) { }

  getProductList(): Observable<Product[]> {

    return this.rest.get<Product[]>(environment.baseURL + 'product/product', { responseType: 'json' });
  }

  createProduct(product: Product,images:File): Observable<Product> {
    let headers = new HttpHeaders();
    //this is the important step. You need to set content type as null
    headers.set('Accept', "multipart/form-data");
    
    const formData: FormData = new FormData();
    formData.append('product',JSON.stringify(product));
   
    console.log("Image"+images);
    console.log("name"+images.name);
    formData.append('files',images,images.name);

    return this.rest.post<Product>(environment.baseURL + 'product', formData,{headers});
  }

}