import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { Observable } from 'rxjs';
import { CommonModule } from '@angular/common';
import { ProductSearchService } from '../../shared/services/product-search.service';
import { Product } from '../../shared/models/product.model';

@Component({
  selector: 'app-search-results',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <div class="container mt-4">
      <h2 class="mb-4">Search Results for "{{ currentSearchTerm }}"</h2>
      
      <div class="row">
        <div *ngFor="let product of products$ | async" class="col-md-3 mb-4">
          <div class="card h-100">
            <img [src]="product.image || 'assets/images/placeholder.jpg'" 
                 class="card-img-top" 
                 alt="{{ product.name }}"
                 style="height: 200px; object-fit: cover;">
            <div class="card-body">
              <h5 class="card-title">{{ product.name }}</h5>
              <p class="card-text">{{ product.description | slice:0:100 }}...</p>
              <div class="d-flex justify-content-between align-items-center">
                <strong class="text-primary">₹{{ product.price }}</strong>
                <a [routerLink]="['/product', product.id]" class="btn btn-outline-primary">View Details</a>
              </div>
            </div>
          </div>
        </div>
        
        <!-- No results message -->
        <div *ngIf="(products$ | async)?.length === 0" class="col-12 text-center mt-5">
          <h3>No products found</h3>
          <p>Try different keywords or browse our categories</p>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .card {
      transition: transform 0.2s ease;
      border: 1px solid #e6e9ed;
      box-shadow: 0 2px 4px rgba(0,0,0,0.05);
    }
    .card:hover {
      transform: translateY(-5px);
      box-shadow: 0 4px 8px rgba(0,0,0,0.1);
    }
    .card-title {
      font-size: 1.1rem;
      margin-bottom: 0.5rem;
    }
    .card-text {
      color: #6c757d;
      font-size: 0.9rem;
    }
  `]
})
export class SearchResultsComponent implements OnInit {
  products$: Observable<Product[]>;
  currentSearchTerm: string = '';

  constructor(
    private searchService: ProductSearchService,
    private route: ActivatedRoute
  ) {
    this.products$ = this.searchService.searchResults();
  }

  ngOnInit() {
    // Get search term from query params
    this.route.queryParams.subscribe(params => {
      const query = params['q'] || '';
      this.currentSearchTerm = query;
      this.searchService.search(query);
    });
  }
}
