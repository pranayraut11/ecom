import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Observable, of } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { ProductSearchService } from '../../shared/services/product-search.service';
import { Product } from '../../shared/models/product.model';
import { CommonModule } from '@angular/common';
import { ProductFilterComponent } from './product-filter.component';

@Component({
  selector: 'app-product-list',
  templateUrl: './product-list.component.html',
  styleUrls: ['./product-list.component.css'],
  imports: [CommonModule, ProductFilterComponent],
  standalone: true
})
export class ProductListComponent implements OnInit {
  products$: Observable<Product[]> = of([]);
  searchTerm: string = '';

  // Filter properties
  categories: string[] = ['Shoes', 'Clothing', 'Accessories']; // Example, replace with API data
  selectedCategory: string = '';
  minPrice: number = 0;
  maxPrice: number = 5000;
  brands: string[] = ['Puma', 'Nike', 'Adidas']; // Example, replace with API data
  selectedBrands: string[] = [];

  constructor(
    private route: ActivatedRoute,
    private searchService: ProductSearchService
  ) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      this.searchTerm = params.get('searchTerm') || '';
      this.products$ = this.searchService.getSearchResultsByName(this.searchTerm);
    });
  }

  onCategoryChange(category: string) {
    this.selectedCategory = category;
    this.filterProducts();
  }

  onPriceChange(price: {min: number, max: number}) {
    this.minPrice = price.min;
    this.maxPrice = price.max;
    this.filterProducts();
  }

  onBrandsChange(brands: string[]) {
    this.selectedBrands = brands;
    this.filterProducts();
  }

//   filterProducts() {
//     // You should call your API with filter params here
//     this.products$ = this.searchService.getFilteredProducts({
//       searchTerm: this.searchTerm,
//       category: this.selectedCategory,
//       minPrice: this.minPrice,
//       maxPrice: this.maxPrice,
//       brands: this.selectedBrands
//     });
//   }
   filterProducts() {
    // You should call your API with filter params here
    this.products$ = this.searchService.getSearchResultsByName(
      this.searchTerm
    );
  }
}
