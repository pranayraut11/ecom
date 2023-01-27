import { Injectable } from "@angular/core";
import { HttpClient, HttpHeaders, HttpParams } from "@angular/common/http";
import { Product } from "src/app/shared/models/product.model";
import { Observable } from "rxjs";
import { environment } from "src/environments/environment";
import { PRODUCT } from "../../constants/ApiEndpoints";

@Injectable({ "providedIn": "root" })
export class ProductRestService {

  constructor(private rest: HttpClient) { }

  getProductList(): Observable<Product[]> {
     const headers = new HttpHeaders()
     .set("X-CustomHeader", "none");
   
    //console.log("Prd"+headers.get('isSecured'))
    return this.rest.get<Product[]>(environment.baseURL + PRODUCT,{headers});
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

    return this.rest.post<Product>(environment.baseURL + PRODUCT, formData,{headers});
  }


  deleteProduct(productIds : string[]):Observable<any>{
   return this.rest.delete(environment.baseURL + PRODUCT,{body:productIds});
  }

}