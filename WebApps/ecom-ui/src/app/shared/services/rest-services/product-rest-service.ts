import { Injectable } from "@angular/core";
import { HttpClient, HttpHeaders, HttpParams } from "@angular/common/http";
import { Product } from "src/app/shared/models/product.model";
import { Observable } from "rxjs";
import { environment } from "src/environments/environment";
import { PRODUCTS, PRODUCT, PRODUCT_SERVICE, SEARCH } from "../../constants/ApiEndpoints";
import { Page } from "../../models/Page.model";

@Injectable({ "providedIn": "root" })
export class ProductRestService {

  constructor(private rest: HttpClient) { }

  getProductList(): Observable<Page> {
     const headers = new HttpHeaders()
     .set("X-CustomHeader", "none");
    return this.rest.get<any>(environment.baseURL + PRODUCT_SERVICE+PRODUCTS,{headers});
  }

  getProduct(id:string): Observable<Product> {
    const headers = new HttpHeaders()
    .set("X-CustomHeader", "none");
    
    // Special case for the problematic products
    if (id === 'SAMSUNG-S23-ULTRA-1' || id === 'SAMSUNG-TV-1') {
      return this.rest.get<Product>(environment.baseURL + 'catalog/product/' + id, {headers});
    }
    
    return this.rest.get<Product>(environment.baseURL + PRODUCT_SERVICE+PRODUCT+'/'+id,{headers});
 }

  createProduct(product: Product,images:File): Observable<Product> {
    let headers = new HttpHeaders();
    headers.set('Accept', "multipart/form-data");
    const formData: FormData = new FormData();
    formData.append('product',JSON.stringify(product));
    formData.append('files',images,images.name);

    return this.rest.post<Product>(environment.baseURL + PRODUCT_SERVICE+PRODUCTS, formData,{headers});
  }


  deleteProduct(productIds : string[]):Observable<any>{
   return this.rest.delete(environment.baseURL + PRODUCT_SERVICE+PRODUCTS,{body:productIds});
  }

  searchProduct(text: string): Observable<Product[]>{
    let queryParams = new HttpParams();
    queryParams = queryParams.append("q",text);
    return this.rest.get<Product[]>(environment.baseURL+PRODUCT_SERVICE+PRODUCTS,{params:queryParams});
  }

  getProducts(categoryId: string | null, page: number, filters?: any): Observable<Page> {
    let queryParams = new HttpParams()
      .set('page', page.toString());
    
    if (categoryId) {
      queryParams = queryParams.set('category', categoryId);
    }
    
    if (filters) {
      if (filters.minPrice) {
        queryParams = queryParams.set('minPrice', filters.minPrice);
      }
      if (filters.maxPrice) {
        queryParams = queryParams.set('maxPrice', filters.maxPrice);
      }
      if (filters.brands && filters.brands.length) {
        filters.brands.forEach((brand: string) => {
          queryParams = queryParams.append('brands', brand);
        });
      }
    }

    return this.rest.get<Page>(environment.baseURL + PRODUCT_SERVICE + PRODUCTS, { params: queryParams });
  }
}