import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, catchError, of } from 'rxjs';
import { UiLayout } from '../models/ui-layout.model';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class LayoutService {
  private baseUrl = `${environment.apiUrl}/ui-service/api/ui/layouts`;

  constructor(private http: HttpClient) {}

  getAllLayouts(): Observable<UiLayout[]> {
    return this.http.get<UiLayout[]>(this.baseUrl).pipe(
      catchError(error => {
        console.error('Error fetching layouts:', error);
        return of([]);
      })
    );
  }

  getLayoutById(id: string): Observable<UiLayout> {
    return this.http.get<UiLayout>(`${this.baseUrl}/${id}`).pipe(
      catchError(error => {
        console.error(`Error fetching layout with id ${id}:`, error);
        return of({} as UiLayout);
      })
    );
  }

  getLayoutByName(name: string): Observable<UiLayout> {
    return this.http.get<UiLayout>(`${this.baseUrl}/name/${name}`).pipe(
      catchError(error => {
        console.error(`Error fetching layout with name ${name}:`, error);
        return of({} as UiLayout);
      })
    );
  }

  getLayoutByRoute(route: string): Observable<UiLayout> {
    // Ensure route starts with a slash
    const formattedRoute = route.startsWith('/') ? route.substring(1) : route;
    return this.http.get<UiLayout>(`${this.baseUrl}/route/${formattedRoute}`).pipe(
      catchError(error => {
        console.error(`Error fetching layout for route ${route}:`, error);
        return of({} as UiLayout);
      })
    );
  }

  getActiveLayouts(): Observable<UiLayout[]> {
    return this.http.get<UiLayout[]>(`${this.baseUrl}/active`).pipe(
      catchError(error => {
        console.error('Error fetching active layouts:', error);
        return of([]);
      })
    );
  }
}
