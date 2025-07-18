import { Component, OnInit, AfterViewInit, OnDestroy, HostListener } from "@angular/core";
import { AuthRestService } from "src/app/shared/services/rest-services/auth-rest-service";
import { AUTH_TOKEN } from "../../constants/AuthConst";
import { CommonModule } from "@angular/common";
import { RouterModule } from "@angular/router";
import { LoginComponent } from "../login/login.component";
import { BootstrapService } from "../../services/bootstrap.service";
import { NavigationEnd, Router } from "@angular/router";
import { filter, Subscription, Observable, of } from "rxjs";
import { AuthService } from "../../../core/auth/services/auth.service";
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { debounceTime, distinctUntilChanged, switchMap, catchError, startWith, take } from 'rxjs/operators';
import { ProductSearchService } from '../../services/product-search.service';
import { Product } from '../../models/product.model';

@Component({
    selector: "app-header",
    templateUrl: "./header.component.html",
    styleUrls: ['./header.component.css'],
    standalone: true,
    imports: [
        CommonModule,
        RouterModule,
        ReactiveFormsModule,
        LoginComponent
    ]
})
export class HeaderComponent implements OnInit, AfterViewInit, OnDestroy {
    
    isAuthenticated: boolean = false;
    isProfileDropdownOpen: boolean = false;
    cartItemCount: number = 0;
    private routerSubscription: Subscription | null = null;
    searchControl = new FormControl('');
    showDropdown = false;
    searchResults$: Observable<Product[]> = of([]);
    
    constructor(
        private authRestService: AuthRestService,
        private bootstrapService: BootstrapService,
        private router: Router,
        private authService: AuthService,
        private searchService: ProductSearchService
    ){
        // Initialize the search results observable with empty array as initial value
        this.searchResults$ = this.searchControl.valueChanges.pipe(
            debounceTime(300),
            distinctUntilChanged(),
            switchMap(term => this.searchService.getSearchResults(term)),
            startWith([])
        );
    }
    
    ngOnInit() {
        // For testing, set isAuthenticated to true
        this.isAuthenticated = true;
        
        if(localStorage.getItem(AUTH_TOKEN)){
            console.log("User is authenticated");
            this.isAuthenticated = true;
        }
        
        // Set sample cart count for demo
        this.updateCartCount();
        
        // Subscribe to router events to reinitialize dropdowns on navigation
        this.routerSubscription = this.router.events
            .pipe(filter(event => event instanceof NavigationEnd))
            .subscribe(() => {
                // Close dropdowns when navigation occurs
                this.isProfileDropdownOpen = false;
                this.showDropdown = false;
                
                // Update cart count on navigation
                this.updateCartCount();
                
                // Initialize bootstrap components after navigation completes
                setTimeout(() => {
                    this.bootstrapService.initializeAllComponents();
                }, 300);
            });
    }

    ngAfterViewInit() {
        // Initialize bootstrap dropdowns with a delay to ensure DOM is ready
        setTimeout(() => {
            console.log('Header: Initializing Bootstrap components');
            this.bootstrapService.initializeAllComponents();
            
            // Dispatch custom event that bootstrap-init.js listens for
            if (typeof document !== 'undefined') {
                document.dispatchEvent(new CustomEvent('route-changed'));
            }
            
            // As a fallback, try to use the global function if available
            if (typeof window !== 'undefined' && typeof window['initBootstrapComponents'] === 'function') {
                window['initBootstrapComponents']();
            }
        }, 300);
    }
    
    // Toggle profile dropdown
    toggleProfileDropdown(event: Event) {
        event.preventDefault();
        event.stopPropagation();
        this.isProfileDropdownOpen = !this.isProfileDropdownOpen;
    }
    
    // Close profile dropdown
    closeProfileDropdown() {
        this.isProfileDropdownOpen = false;
    }
    
    // Close dropdowns when clicking outside
    @HostListener('document:click', ['$event'])
    onDocumentClick(event: MouseEvent) {
        const target = event.target as HTMLElement;
        if (!target.closest('.dropdown') && this.isProfileDropdownOpen) {
            this.isProfileDropdownOpen = false;
        }
        
        const searchContainer = document.querySelector('.search-container');
        if (searchContainer && !searchContainer.contains(event.target as Node)) {
            this.showDropdown = false;
        }
    }
    
    ngOnDestroy() {
        // Clean up subscription
        if (this.routerSubscription) {
            this.routerSubscription.unsubscribe();
            this.routerSubscription = null;
        }
    }
    
    // Method to update cart count
    updateCartCount(): void {
        // In a real application, this would fetch from a cart service
        // For demo purposes, let's set a random number between 0 and 5
        this.cartItemCount = Math.floor(Math.random() * 6);
    }
    
    selectProduct(product: Product): void {
        this.showDropdown = false;
        this.searchControl.setValue('');
        this.router.navigate(['/product', product.id]);
    }

    onSearch(): void {
        console.log('Search initiated:', this.searchControl.value);
        this.searchResults$ = this.searchService.getSearchResults(this.searchControl.value);
    }
}