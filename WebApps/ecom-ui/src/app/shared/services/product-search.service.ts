
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { environment } from 'src/environments/environment';
import { Product } from '../models/product.model';

@Injectable({
  providedIn: 'root'
})
export class ProductSearchService {
  private apiUrl = `${environment.baseURL.replace(/\/+$/, '')}/product-service`;

  constructor(private http: HttpClient) { }

  // Get search results
  getSearchResults(query: string): Observable<Product[]> {
    if (!query || query.trim().length < 2) {
      return of([]);
    }
    // Ensure only one slash between base URL and endpoint
    const url = `${this.apiUrl.replace(/\/+$/, '')}/search/${query.trim()}`;
    return this.http.get<Product[]>(url);
  }

   getSearchResultsByName(query: string): Observable<Product[]> {
    if (!query || query.trim().length < 2) {
      return of([]);
    }
    // Ensure only one slash between base URL and endpoint
    const url = `${this.apiUrl.replace(/\/+$/, '')}/searchbyname/${query.trim()}`;
    return this.http.get<Product[]>(url);
  }
  // Get filtered products
  getFilteredProducts(filters: {
    searchTerm?: string;
    category?: string;
    minPrice?: number;
    maxPrice?: number;
    brands?: string[];
  }): Observable<Product[]> {
    const params: any = {};
    if (filters.searchTerm) params.q = filters.searchTerm;
    if (filters.category) params.category = filters.category;
    if (filters.minPrice !== undefined) params.minPrice = filters.minPrice;
    if (filters.maxPrice !== undefined) params.maxPrice = filters.maxPrice;
    if (filters.brands && filters.brands.length > 0) params.brands = filters.brands.join(',');

    // Example endpoint, adjust as needed
    const url = `${this.apiUrl.replace(/\/+$/, '')}/search/filtered`;
    return this.http.get<Product[]>(url, { params }).pipe(
      catchError(() => of([]))
    );
  }
}
