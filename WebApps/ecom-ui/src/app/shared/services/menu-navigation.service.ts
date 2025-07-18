import { Injectable } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { filter } from 'rxjs/operators';
import { BootstrapService } from './bootstrap.service';

@Injectable({
  providedIn: 'root'
})
export class MenuNavigationService {
  
  constructor(
    private router: Router,
    private bootstrapService: BootstrapService
  ) {
    // Listen for route changes to initialize dropdowns after navigation
    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe(() => {
      this.initializeBootstrapComponents();
      // Dispatch custom event that our bootstrap-init.js is listening for
      document.dispatchEvent(new CustomEvent('route-changed'));
    });
  }

  /**
   * Navigate to a category or subcategory page
   */
  navigateToCategory(categoryName: string): void {
    this.router.navigate(['/catalog/category', categoryName]);
  }

  /**
   * Navigate to a product list page
   */
  navigateToList(subCategoryName: string): void {
    this.router.navigate(['/catalog/list', subCategoryName]);
  }

  /**
   * Initialize Bootstrap components like dropdowns
   */
  initializeBootstrapComponents(): void {
    this.bootstrapService.initializeAllComponents();
  }
}
