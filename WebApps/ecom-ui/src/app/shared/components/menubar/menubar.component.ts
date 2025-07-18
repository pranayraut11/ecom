import { Component, OnInit, AfterViewInit, OnDestroy, HostListener } from '@angular/core';
import { Menu } from '../../models/menu.model';
import { MenusRestService } from '../../services/rest-services/menus-rest-service';
import { MenuNavigationService } from '../../services/menu-navigation.service';
import { BootstrapService } from '../../services/bootstrap.service';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { NavigationEnd, Router } from '@angular/router';
import { filter, Subscription } from 'rxjs';

@Component({
  selector: 'app-menubar',
  templateUrl: './menubar.component.html',
  styleUrls: ['./menubar.component.css'],
  standalone: true,
  imports: [
    CommonModule,
    RouterModule
  ]
})
export class MenubarComponent implements OnInit, AfterViewInit, OnDestroy {

  categories: Menu[] = [];
  openDropdownIndex: number = -1; // -1 means no dropdown is open
  private routerSubscription: Subscription | null = null;
  
  constructor(
    private menuRest: MenusRestService,
    private menuNavService: MenuNavigationService,
    private bootstrapService: BootstrapService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.loadMenu();
    
    // Subscribe to router events to close dropdowns on navigation
    this.routerSubscription = this.router.events
      .pipe(filter(event => event instanceof NavigationEnd))
      .subscribe(() => {
        // Close all dropdowns when navigation occurs
        this.closeAllDropdowns();
      });
  }

  ngAfterViewInit(): void {
    this.initializeDropdowns();
  }
  
  ngOnDestroy(): void {
    // Clean up subscription
    if (this.routerSubscription) {
      this.routerSubscription.unsubscribe();
      this.routerSubscription = null;
    }
  }

  // Toggle dropdown for a specific category
  toggleCategoryDropdown(index: number, event: Event): void {
    event.preventDefault();
    event.stopPropagation();
    
    if (this.openDropdownIndex === index) {
      // If clicking the same dropdown that's already open, close it
      this.openDropdownIndex = -1;
    } else {
      // Otherwise, open this dropdown and close others
      this.openDropdownIndex = index;
    }
  }
  
  // Close all dropdowns
  closeAllDropdowns(): void {
    this.openDropdownIndex = -1;
  }
  
  // Close dropdowns when clicking outside
  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent) {
    const target = event.target as HTMLElement;
    if (!target.closest('.nav-item.dropdown') && this.openDropdownIndex !== -1) {
      this.closeAllDropdowns();
    }
  }

  initializeDropdowns(): void {
    // Initialize all dropdowns after menu data is loaded
    setTimeout(() => {
      console.log('Menubar: Initializing Bootstrap components');
      this.bootstrapService.initializeAllComponents();
    }, 300);
  }

  loadMenu() {
    console.log("Loading Menus");
    this.menuRest.getMenus().subscribe({
      next: (response) => {
        this.categories = this.processMenuData(response);
        console.log("Menu data loaded:", this.categories);
      },
      error: (error) => {
        console.error("Error loading menus:", error);
      }
    });
  }

  navigateToCategory(categoryName: string): void {
    this.menuNavService.navigateToCategory(categoryName);
  }

  navigateToList(subCategoryName: string): void {
    this.menuNavService.navigateToList(subCategoryName);
  }

  private processMenuData(menus: any[]): any[] {
    return menus.map(menu => {
      if (menu.subCategories) {
        menu.subCategories = this.processMenuData(menu.subCategories);
      }
      return menu;
    });
  }
}
