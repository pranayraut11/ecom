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
}
