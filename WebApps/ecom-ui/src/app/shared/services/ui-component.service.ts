import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of, BehaviorSubject } from 'rxjs';
import { catchError, map, tap } from 'rxjs/operators';
import { ApiEndpoints } from '../constants/ApiEndpoints';

/**
 * Interfaces for new UI component structure
 */
export interface UiComponentDto {
  id?: string;
  name: string;
  displayName: string;
  componentType: string;
  properties: any;
  isActive: boolean;
  orderIndex: number;
}

export interface UiLayoutDto {
  id?: string;
  name: string;
  displayName: string;
  components: string[];
  route: string;
  theme: string;
  isActive: boolean;
}

export interface UiThemeDto {
  id?: string;
  name: string;
  primaryColor: string;
  secondaryColor: string;
  backgroundColor: string;
  textColor: string;
  linkColor: string;
  headerBackgroundColor: string;
  headerTextColor: string;
  footerBackgroundColor: string;
  footerTextColor: string;
}

/**
 * Service for managing UI component data and structures
 * This service provides a centralized way to manage UI components across the application
 */
@Injectable({
  providedIn: 'root'
})
export class UiComponentService {
  // Store loaded components in memory
  private componentsCache = new Map<string, UiComponentDto>();
  private layoutsCache = new Map<string, UiLayoutDto>();
  private themesCache = new Map<string, UiThemeDto>();
  
  // BehaviorSubjects for reactive updates
  private componentsSubject = new BehaviorSubject<Map<string, UiComponentDto>>(new Map());
  private layoutsSubject = new BehaviorSubject<Map<string, UiLayoutDto>>(new Map());
  private themesSubject = new BehaviorSubject<Map<string, UiThemeDto>>(new Map());
  
  // Observable streams that components can subscribe to
  public components$ = this.componentsSubject.asObservable();
  public layouts$ = this.layoutsSubject.asObservable();
  public themes$ = this.themesSubject.asObservable();

  constructor(private http: HttpClient) {
    // Initialize by loading active components, layouts and themes
    this.loadActiveComponents();
    this.loadActiveLayouts();
    this.loadAllThemes();
  }

  /**
   * Load all active UI components
   */
  loadActiveComponents(): void {
    this.http.get<UiComponentDto[]>(`${ApiEndpoints.UI_COMPONENTS}/active`)
      .pipe(
        catchError(error => {
          console.error('Error loading active UI components', error);
          return of([]);
        })
      )
      .subscribe(components => {
        components.forEach(component => {
          this.componentsCache.set(component.id, component);
        });
        this.componentsSubject.next(this.componentsCache);
      });
  }

  /**
   * Get a component by ID
   */
  getComponent(id: string): Observable<UiComponentDto | null> {
    // Check if component is in cache
    if (this.componentsCache.has(id)) {
      return of(this.componentsCache.get(id));
    }

    // If not in cache, try to load from server
    return this.http.get<UiComponentDto>(`${ApiEndpoints.UI_COMPONENTS}/${id}`)
      .pipe(
        tap(component => {
          // Add to cache
          this.componentsCache.set(id, component);
          this.componentsSubject.next(this.componentsCache);
        }),
        catchError(error => {
          console.error(`Error fetching component with id ${id}`, error);
          return of(null);
        })
      );
  }

  /**
   * Get components by type
   */
  getComponentsByType(type: string): Observable<UiComponentDto[]> {
    return this.http.get<UiComponentDto[]>(`${ApiEndpoints.UI_COMPONENTS}/type/${type}`)
      .pipe(
        tap(components => {
          // Add to cache
          components.forEach(component => {
            this.componentsCache.set(component.id, component);
          });
          this.componentsSubject.next(this.componentsCache);
        }),
        catchError(error => {
          console.error(`Error fetching components of type ${type}`, error);
          return of([]);
        })
      );
  }

  /**
   * Load all active layouts
   */
  loadActiveLayouts(): void {
    this.http.get<UiLayoutDto[]>(`${ApiEndpoints.UI_LAYOUTS}/active`)
      .pipe(
        catchError(error => {
          console.error('Error loading active layouts', error);
          return of([]);
        })
      )
      .subscribe(layouts => {
        layouts.forEach(layout => {
          this.layoutsCache.set(layout.id, layout);
        });
        this.layoutsSubject.next(this.layoutsCache);
      });
  }

  /**
   * Get a layout by ID
   */
  getLayout(id: string): Observable<UiLayoutDto | null> {
    // Check if layout is in cache
    if (this.layoutsCache.has(id)) {
      return of(this.layoutsCache.get(id));
    }

    // If not in cache, try to load from server
    return this.http.get<UiLayoutDto>(`${ApiEndpoints.UI_LAYOUTS}/${id}`)
      .pipe(
        tap(layout => {
          // Add to cache
          this.layoutsCache.set(id, layout);
          this.layoutsSubject.next(this.layoutsCache);
        }),
        catchError(error => {
          console.error(`Error fetching layout with id ${id}`, error);
          return of(null);
        })
      );
  }

  /**
   * Get layout by route
   */
  getLayoutByRoute(route: string): Observable<UiLayoutDto | null> {
    return this.http.get<UiLayoutDto>(`${ApiEndpoints.UI_LAYOUTS}/route/${route}`)
      .pipe(
        tap(layout => {
          if (layout) {
            // Add to cache
            this.layoutsCache.set(layout.id, layout);
            this.layoutsSubject.next(this.layoutsCache);
          }
        }),
        catchError(error => {
          console.error(`Error fetching layout for route ${route}`, error);
          return of(null);
        })
      );
  }

  /**
   * Load all themes
   */
  loadAllThemes(): void {
    this.http.get<UiThemeDto[]>(`${ApiEndpoints.UI_THEMES}`)
      .pipe(
        catchError(error => {
          console.error('Error loading themes', error);
          return of([]);
        })
      )
      .subscribe(themes => {
        themes.forEach(theme => {
          this.themesCache.set(theme.id, theme);
        });
        this.themesSubject.next(this.themesCache);
      });
  }

  /**
   * Get theme by name
   */
  getThemeByName(name: string): Observable<UiThemeDto | null> {
    // First check if any theme in cache matches the name
    const cachedTheme = Array.from(this.themesCache.values()).find(theme => theme.name === name);
    if (cachedTheme) {
      return of(cachedTheme);
    }

    // If not in cache, try to load from server
    return this.http.get<UiThemeDto>(`${ApiEndpoints.UI_THEMES}/name/${name}`)
      .pipe(
        tap(theme => {
          if (theme) {
            // Add to cache
            this.themesCache.set(theme.id, theme);
            this.themesSubject.next(this.themesCache);
          }
        }),
        catchError(error => {
          console.error(`Error fetching theme with name ${name}`, error);
          return of(null);
        })
      );
  }

  /**
   * Update a component
   */
  updateComponent(component: UiComponentDto): Observable<UiComponentDto> {
    return this.http.put<UiComponentDto>(
      `${ApiEndpoints.UI_COMPONENTS}/${component.id}`,
      component
    ).pipe(
      tap(updatedComponent => {
        // Update cache
        this.componentsCache.set(updatedComponent.id, updatedComponent);
        this.componentsSubject.next(this.componentsCache);
      }),
      catchError(error => {
        console.error(`Error updating component ${component.id}`, error);
        throw error;
      })
    );
  }

  /**
   * Update a layout
   */
  updateLayout(layout: UiLayoutDto): Observable<UiLayoutDto> {
    return this.http.put<UiLayoutDto>(
      `${ApiEndpoints.UI_LAYOUTS}/${layout.id}`,
      layout
    ).pipe(
      tap(updatedLayout => {
        // Update cache
        this.layoutsCache.set(updatedLayout.id, updatedLayout);
        this.layoutsSubject.next(this.layoutsCache);
      }),
      catchError(error => {
        console.error(`Error updating layout ${layout.id}`, error);
        throw error;
      })
    );
  }

  /**
   * Create a new component
   */
  createComponent(component: UiComponentDto): Observable<UiComponentDto> {
    return this.http.post<UiComponentDto>(
      `${ApiEndpoints.UI_COMPONENTS}`,
      component
    ).pipe(
      tap(newComponent => {
        // Update cache
        this.componentsCache.set(newComponent.id, newComponent);
        this.componentsSubject.next(this.componentsCache);
      }),
      catchError(error => {
        console.error('Error creating component', error);
        throw error;
      })
    );
  }

  /**
   * Create a new layout
   */
  createLayout(layout: UiLayoutDto): Observable<UiLayoutDto> {
    return this.http.post<UiLayoutDto>(
      `${ApiEndpoints.UI_LAYOUTS}`,
      layout
    ).pipe(
      tap(newLayout => {
        // Update cache
        this.layoutsCache.set(newLayout.id, newLayout);
        this.layoutsSubject.next(this.layoutsCache);
      }),
      catchError(error => {
        console.error('Error creating layout', error);
        throw error;
      })
    );
  }
}
          console.error(`Error loading component with ID: ${id}`, error);
          return of(null);
        })
      );
  }

  /**
   * Get components by type
   */
  getComponentsByType(type: string): Observable<UiComponentConfig[]> {
    return this.http.get<UiComponentConfig[]>(`${ApiEndpoints.UI_COMPONENTS}/type/${type}`)
      .pipe(
        tap(components => {
          // Add components to cache
          components.forEach(component => {
            this.componentsCache.set(component.id, component);
          });
          this.componentsSubject.next(this.componentsCache);
        }),
        catchError(error => {
          console.error(`Error loading components of type: ${type}`, error);
          return of([]);
        })
      );
  }

  /**
   * Get a layout configuration by ID
   */
  getLayout(id: string): Observable<LayoutConfig | null> {
    // Check if layout is in cache
    if (this.layoutsCache.has(id)) {
      return of(this.layoutsCache.get(id));
    }

    // If not in cache, try to load from server
    return this.http.get<LayoutConfig>(`${ApiEndpoints.UI_LAYOUTS}/${id}`)
      .pipe(
        tap(layout => {
          // Add to cache
          this.layoutsCache.set(id, layout);
          this.layoutsSubject.next(this.layoutsCache);
        }),
        catchError(error => {
          console.error(`Error loading layout with ID: ${id}`, error);
          return of(null);
        })
      );
  }

  /**
   * Update a component configuration
   */
  updateComponent(component: UiComponentConfig): Observable<UiComponentConfig> {
    return this.http.put<UiComponentConfig>(
      `${ApiEndpoints.UI_COMPONENTS}/${component.id}`, 
      component
    ).pipe(
      tap(updatedComponent => {
        // Update cache
        this.componentsCache.set(updatedComponent.id, updatedComponent);
        this.componentsSubject.next(this.componentsCache);
      }),
      catchError(error => {
        console.error(`Error updating component with ID: ${component.id}`, error);
        return of(component); // Return original on error
      })
    );
  }

  /**
   * Update a layout configuration
   */
  updateLayout(layout: LayoutConfig): Observable<LayoutConfig> {
    return this.http.put<LayoutConfig>(
      `${ApiEndpoints.UI_LAYOUTS}/${layout.id}`, 
      layout
    ).pipe(
      tap(updatedLayout => {
        // Update cache
        this.layoutsCache.set(updatedLayout.id, updatedLayout);
        this.layoutsSubject.next(this.layoutsCache);
      }),
      catchError(error => {
        console.error(`Error updating layout with ID: ${layout.id}`, error);
        return of(layout); // Return original on error
      })
    );
  }

  /**
   * Create a new component
   */
  createComponent(component: UiComponentConfig): Observable<UiComponentConfig> {
    return this.http.post<UiComponentConfig>(
      ApiEndpoints.UI_COMPONENTS, 
      component
    ).pipe(
      tap(newComponent => {
        // Add to cache
        this.componentsCache.set(newComponent.id, newComponent);
        this.componentsSubject.next(this.componentsCache);
      }),
      catchError(error => {
        console.error('Error creating component', error);
        return of(null);
      })
    );
  }

  /**
   * Create a new layout
   */
  createLayout(layout: LayoutConfig): Observable<LayoutConfig> {
    return this.http.post<LayoutConfig>(
      ApiEndpoints.UI_LAYOUTS, 
      layout
    ).pipe(
      tap(newLayout => {
        // Add to cache
        this.layoutsCache.set(newLayout.id, newLayout);
        this.layoutsSubject.next(this.layoutsCache);
      }),
      catchError(error => {
        console.error('Error creating layout', error);
        return of(null);
      })
    );
  }

  /**
   * Get all components for a specific page or section
   */
  getPageComponents(pageId: string): Observable<UiComponentConfig[]> {
    return this.http.get<UiComponentConfig[]>(`${ApiEndpoints.UI_COMPONENTS}/page/${pageId}`)
      .pipe(
        tap(components => {
          // Add components to cache
          components.forEach(component => {
            this.componentsCache.set(component.id, component);
          });
          this.componentsSubject.next(this.componentsCache);
        }),
        catchError(error => {
          console.error(`Error loading components for page: ${pageId}`, error);
          return of([]);
        })
      );
  }

  /**
   * Get a fallback component configuration when loading fails
   */
  getFallbackComponent(type: string): UiComponentConfig {
    return {
      id: `fallback-${Date.now()}`,
      type: type,
      label: 'Unavailable Component',
      visible: true,
      enabled: false,
      style: { opacity: 0.7 }
    };
  }
}
