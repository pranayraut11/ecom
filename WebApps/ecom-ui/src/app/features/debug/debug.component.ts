import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CategoryRestService } from 'src/app/shared/services/rest-services/category-rest-service';
import { ProductRestService } from 'src/app/shared/services/rest-services/product-rest-service';

@Component({
  selector: 'app-debug',
  template: `
    <div class="debug-container">
      <h2>Image URL Debugger</h2>
      
      <div class="section">
        <h3>Category Images</h3>
        <div *ngFor="let category of categories">
          <h4>{{ category.name }}</h4>
          <p>Image URL: {{ category.imageUrl }}</p>
          <img [src]="category.imageUrl" width="100" height="100" alt="Category image">
          
          <div class="subcategories" *ngIf="category.subCategories">
            <h5>Subcategories</h5>
            <div *ngFor="let subCategory of category.subCategories">
              <p>{{ subCategory.name }} - Image URL: {{ subCategory.imageUrl }}</p>
              <img [src]="subCategory.imageUrl || subCategory.url" width="80" height="80" alt="Subcategory image">
            </div>
          </div>
        </div>
      </div>
      
      <div class="section">
        <h3>Product Images</h3>
        <div *ngFor="let product of products">
          <h4>{{ product.name }}</h4>
          <div *ngIf="product.images && product.images.length">
            <p>First Image URL: {{ product.images[0] }}</p>
            <img [src]="product.images[0]" width="100" height="100" alt="Product image">
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .debug-container {
      padding: 20px;
      font-family: Arial, sans-serif;
    }
    .section {
      margin-bottom: 30px;
      padding: 15px;
      border: 1px solid #ddd;
      border-radius: 5px;
    }
    img {
      border: 1px solid #ccc;
      margin: 5px;
      object-fit: contain;
      background-color: #f5f5f5;
    }
    h3 {
      color: #333;
      border-bottom: 1px solid #eee;
      padding-bottom: 10px;
    }
    .subcategories {
      margin-left: 20px;
      padding: 10px;
      background-color: #f9f9f9;
      border-radius: 5px;
    }
  `],
  standalone: true,
  imports: [CommonModule]
})
export class DebugComponent implements OnInit {
  categories: any[] = [];
  products: any[] = [];

  constructor(
    private categoryService: CategoryRestService,
    private productService: ProductRestService
  ) {}

  ngOnInit(): void {
    // Load categories
    this.categoryService.getCategories().subscribe(categories => {
      this.categories = categories;
      console.log('Categories loaded:', categories);
    }, error => {
      console.error('Error loading categories:', error);
    });

    // Load products
    this.productService.getProducts(null, 0).subscribe((response: any) => {
      this.products = response.data || [];
      console.log('Products loaded:', this.products);
    }, error => {
      console.error('Error loading products:', error);
    });
  }
}
