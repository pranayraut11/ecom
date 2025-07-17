import { HttpClient, HttpHeaders } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable, tap } from "rxjs";
import { environment } from "src/environments/environment";
import { Product } from "../../models/product.model";
import { CART_SERVICE, CATEGORY_SERVICE } from "../../constants/ApiEndpoints";
import { SubCategory } from "../../models/SubCategory.model";
import { Category } from "../../models/Category.model";

@Injectable({providedIn:"root"})
export class CategoryRestService{

    constructor(private rest: HttpClient){}

    getCategories(): Observable<Category[]> {
        console.log('Fetching categories from:', environment.baseURL + CATEGORY_SERVICE);
        const headers = new HttpHeaders()
        .set("X-CustomHeader", "none");
        
        return this.rest.get<Category[]>(environment.baseURL + CATEGORY_SERVICE, {
          headers,
          responseType: 'json'
        }).pipe(
          tap(categories => console.log('Categories fetched:', categories))
        );
    }
}