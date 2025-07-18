import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, catchError, of } from 'rxjs';
import { UiComponent } from '../models/ui-component.model';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ComponentService {
  private baseUrl = `${environment.apiUrl}/ui-service/api/ui/components`;

  constructor(private http: HttpClient) {}

  getAllComponents(): Observable<UiComponent[]> {
    return this.http.get<UiComponent[]>(this.baseUrl).pipe(
      catchError(error => {
        console.error('Error fetching components:', error);
        return of([]);
      })
    );
  }

  getComponentById(id: string): Observable<UiComponent> {
    return this.http.get<UiComponent>(`${this.baseUrl}/${id}`).pipe(
      catchError(error => {
        console.error(`Error fetching component with id ${id}:`, error);
        return of({} as UiComponent);
      })
    );
  }

  getComponentsByType(type: string): Observable<UiComponent[]> {
    return this.http.get<UiComponent[]>(`${this.baseUrl}/type/${type}`).pipe(
      catchError(error => {
        console.error(`Error fetching components of type ${type}:`, error);
        return of([]);
      })
    );
  }

  getActiveComponents(): Observable<UiComponent[]> {
    return this.http.get<UiComponent[]>(`${this.baseUrl}/active`).pipe(
      catchError(error => {
        console.error('Error fetching active components:', error);
        return of([]);
      })
    );
  }
}
