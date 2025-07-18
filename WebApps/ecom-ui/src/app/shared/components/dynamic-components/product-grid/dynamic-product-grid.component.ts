import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { BaseComponent } from '../base.component';

interface Product {
  id: number;
  name: string;
  price: number;
  discountPrice?: number;
  image: string;
  rating: number;
  reviewCount: number;
  isNew?: boolean;
  isSale?: boolean;
}

@Component({
  selector: 'app-dynamic-product-grid',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <div class="product-grid-container" [ngStyle]="getStyleObject()">
      <div class="container">
        <div class="row">
          <div *ngFor="let product of displayedProducts" 
               [ngClass]="'col-' + (12 / getProperty('columns', 4))"
               class="mb-4">
            <div class="product-card">
              <div class="product-badges">
                <span class="badge badge-new" *ngIf="product.isNew">New</span>
                <span class="badge badge-sale" *ngIf="product.isSale">Sale</span>
              </div>
              
              <div class="product-image">
                <a [routerLink]="['/product', product.id]">
                  <img [src]="product.image" [alt]="product.name">
                </a>
                <div class="product-actions" *ngIf="getProperty('showAddToCart', true)">
                  <button class="btn-add-to-cart">
                    <i class="bi bi-cart-plus"></i>
                  </button>
                  <button class="btn-wishlist" *ngIf="getProperty('showWishlist', true)">
                    <i class="bi bi-heart"></i>
                  </button>
                </div>
              </div>
              
              <div class="product-info">
                <h3 class="product-title">
                  <a [routerLink]="['/product', product.id]">{{ product.name }}</a>
                </h3>
                
                <div class="product-price" *ngIf="getProperty('showPrices', true)">
                  <span class="current-price" [ngClass]="{'has-discount': product.discountPrice}">
                    {{ product.discountPrice ? product.discountPrice : product.price | currency }}
                  </span>
                  <span class="original-price" *ngIf="product.discountPrice">
                    {{ product.price | currency }}
                  </span>
                </div>
                
                <div class="product-rating" *ngIf="getProperty('showRatings', true)">
                  <div class="stars">
                    <i *ngFor="let star of getStars(product.rating)" 
                       class="bi" 
                       [ngClass]="star === 1 ? 'bi-star-fill' : (star === 0.5 ? 'bi-star-half' : 'bi-star')">
                    </i>
                  </div>
                  <span class="rating-count">({{ product.reviewCount }})</span>
                </div>
              </div>
            </div>
          </div>
        </div>
        
        <!-- Pagination -->
        <div class="row" *ngIf="totalPages > 1">
          <div class="col-12">
            <nav aria-label="Product pagination">
              <ul class="pagination justify-content-center">
                <li class="page-item" [ngClass]="{'disabled': currentPage === 1}">
                  <a class="page-link" href="javascript:void(0)" (click)="changePage(currentPage - 1)">
                    <i class="bi bi-chevron-left"></i>
                  </a>
                </li>
                <li class="page-item" *ngFor="let page of getPageNumbers()" 
                    [ngClass]="{'active': page === currentPage}">
                  <a class="page-link" href="javascript:void(0)" (click)="changePage(page)">
                    {{ page }}
                  </a>
                </li>
                <li class="page-item" [ngClass]="{'disabled': currentPage === totalPages}">
                  <a class="page-link" href="javascript:void(0)" (click)="changePage(currentPage + 1)">
                    <i class="bi bi-chevron-right"></i>
                  </a>
                </li>
              </ul>
            </nav>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .product-grid-container {
      padding: 2rem 0;
    }
    
    .product-card {
      background-color: white;
      border-radius: 8px;
      box-shadow: 0 2px 10px rgba(0,0,0,0.05);
      transition: transform 0.3s ease, box-shadow 0.3s ease;
      height: 100%;
      display: flex;
      flex-direction: column;
      position: relative;
      overflow: hidden;
    }
    
    .product-card:hover {
      transform: translateY(-5px);
      box-shadow: 0 5px 15px rgba(0,0,0,0.1);
    }
    
    .product-badges {
      position: absolute;
      top: 10px;
      left: 10px;
      z-index: 2;
    }
    
    .badge {
      display: inline-block;
      padding: 5px 10px;
      margin-right: 5px;
      border-radius: 3px;
      font-size: 0.75rem;
      font-weight: 600;
      text-transform: uppercase;
    }
    
    .badge-new {
      background-color: var(--primary-color, #3f51b5);
      color: white;
    }
    
    .badge-sale {
      background-color: var(--secondary-color, #f50057);
      color: white;
    }
    
    .product-image {
      position: relative;
      padding-top: 100%;
      overflow: hidden;
    }
    
    .product-image img {
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      object-fit: cover;
      transition: transform 0.5s ease;
    }
    
    .product-card:hover .product-image img {
      transform: scale(1.05);
    }
    
    .product-actions {
      position: absolute;
      bottom: -50px;
      left: 0;
      right: 0;
      background-color: rgba(255,255,255,0.9);
      display: flex;
      justify-content: center;
      padding: 10px 0;
      transition: bottom 0.3s ease;
    }
    
    .product-card:hover .product-actions {
      bottom: 0;
    }
    
    .product-actions button {
      width: 40px;
      height: 40px;
      border-radius: 50%;
      border: none;
      margin: 0 5px;
      display: flex;
      justify-content: center;
      align-items: center;
      transition: all 0.2s ease;
      cursor: pointer;
    }
    
    .btn-add-to-cart {
      background-color: var(--primary-color, #3f51b5);
      color: white;
    }
    
    .btn-add-to-cart:hover {
      background-color: var(--secondary-color, #f50057);
    }
    
    .btn-wishlist {
      background-color: #f5f5f5;
      color: #333;
    }
    
    .btn-wishlist:hover {
      background-color: var(--secondary-color, #f50057);
      color: white;
    }
    
    .product-info {
      padding: 15px;
      flex-grow: 1;
      display: flex;
      flex-direction: column;
    }
    
    .product-title {
      font-size: 1rem;
      margin-bottom: 10px;
      line-height: 1.4;
    }
    
    .product-title a {
      color: var(--text-color, #333);
      text-decoration: none;
    }
    
    .product-title a:hover {
      color: var(--primary-color, #3f51b5);
    }
    
    .product-price {
      margin-bottom: 10px;
      display: flex;
      align-items: center;
      gap: 8px;
    }
    
    .current-price {
      font-size: 1.1rem;
      font-weight: 700;
      color: var(--text-color, #333);
    }
    
    .current-price.has-discount {
      color: var(--secondary-color, #f50057);
    }
    
    .original-price {
      font-size: 0.9rem;
      text-decoration: line-through;
      color: #999;
    }
    
    .product-rating {
      display: flex;
      align-items: center;
      margin-top: auto;
    }
    
    .stars {
      color: #ffc107;
      margin-right: 5px;
    }
    
    .rating-count {
      font-size: 0.8rem;
      color: #777;
    }
    
    .pagination {
      margin-top: 2rem;
    }
    
    .page-link {
      color: var(--primary-color, #3f51b5);
      border-color: #dee2e6;
    }
    
    .page-item.active .page-link {
      background-color: var(--primary-color, #3f51b5);
      border-color: var(--primary-color, #3f51b5);
    }
    
    .page-link:hover {
      color: var(--secondary-color, #f50057);
    }
    
    @media (max-width: 767px) {
      .product-title {
        font-size: 0.9rem;
      }
      
      .current-price {
        font-size: 1rem;
      }
    }
  `]
})
export class DynamicProductGridComponent extends BaseComponent implements OnInit {
  allProducts: Product[] = [];
  displayedProducts: Product[] = [];
  
  currentPage: number = 1;
  totalPages: number = 1;
  itemsPerPage: number = 12;
  
  ngOnInit(): void {
    this.itemsPerPage = this.getProperty('itemsPerPage', 12);
    
    // Demo products - in a real app, these would come from a service
    this.allProducts = this.getDemoProducts();
    this.totalPages = Math.ceil(this.allProducts.length / this.itemsPerPage);
    this.updateDisplayedProducts();
  }
  
  changePage(page: number): void {
    if (page < 1 || page > this.totalPages) {
      return;
    }
    this.currentPage = page;
    this.updateDisplayedProducts();
  }
  
  updateDisplayedProducts(): void {
    const startIndex = (this.currentPage - 1) * this.itemsPerPage;
    const endIndex = startIndex + this.itemsPerPage;
    this.displayedProducts = this.allProducts.slice(startIndex, endIndex);
  }
  
  getStars(rating: number): number[] {
    const stars: number[] = [];
    const fullStars = Math.floor(rating);
    const hasHalfStar = rating % 1 >= 0.5;
    
    // Add full stars
    for (let i = 0; i < fullStars; i++) {
      stars.push(1);
    }
    
    // Add half star if needed
    if (hasHalfStar) {
      stars.push(0.5);
    }
    
    // Fill the rest with empty stars
    while (stars.length < 5) {
      stars.push(0);
    }
    
    return stars;
  }
  
  getPageNumbers(): number[] {
    const pages: number[] = [];
    const maxVisiblePages = 5;
    
    if (this.totalPages <= maxVisiblePages) {
      // Show all pages
      for (let i = 1; i <= this.totalPages; i++) {
        pages.push(i);
      }
    } else {
      // Calculate range of visible pages
      let startPage = Math.max(1, this.currentPage - Math.floor(maxVisiblePages / 2));
      let endPage = startPage + maxVisiblePages - 1;
      
      if (endPage > this.totalPages) {
        endPage = this.totalPages;
        startPage = Math.max(1, endPage - maxVisiblePages + 1);
      }
      
      for (let i = startPage; i <= endPage; i++) {
        pages.push(i);
      }
    }
    
    return pages;
  }
  
  private getDemoProducts(): Product[] {
    return [
      {
        id: 1,
        name: 'Wireless Bluetooth Headphones',
        price: 149.99,
        discountPrice: 129.99,
        image: 'assets/images/products/headphones.jpg',
        rating: 4.5,
        reviewCount: 123,
        isNew: true
      },
      {
        id: 2,
        name: 'Smart 4K Ultra HD TV',
        price: 799.99,
        image: 'assets/images/products/tv.jpg',
        rating: 4.8,
        reviewCount: 85
      },
      {
        id: 3,
        name: 'Smartphone with Triple Camera',
        price: 699.99,
        discountPrice: 649.99,
        image: 'assets/images/products/smartphone.jpg',
        rating: 4.2,
        reviewCount: 216,
        isSale: true
      },
      {
        id: 4,
        name: 'Laptop with SSD Storage',
        price: 1299.99,
        image: 'assets/images/products/laptop.jpg',
        rating: 4.7,
        reviewCount: 104
      },
      {
        id: 5,
        name: 'Wireless Charging Pad',
        price: 39.99,
        discountPrice: 29.99,
        image: 'assets/images/products/charger.jpg',
        rating: 4.0,
        reviewCount: 78,
        isSale: true
      },
      {
        id: 6,
        name: 'Smart Home Speaker',
        price: 129.99,
        image: 'assets/images/products/speaker.jpg',
        rating: 4.3,
        reviewCount: 92,
        isNew: true
      },
      {
        id: 7,
        name: 'Digital Camera Kit',
        price: 599.99,
        image: 'assets/images/products/camera.jpg',
        rating: 4.6,
        reviewCount: 67
      },
      {
        id: 8,
        name: 'Gaming Console',
        price: 499.99,
        image: 'assets/images/products/console.jpg',
        rating: 4.9,
        reviewCount: 189
      },
      {
        id: 9,
        name: 'Fitness Tracker Watch',
        price: 89.99,
        image: 'assets/images/products/watch.jpg',
        rating: 4.4,
        reviewCount: 156,
        isNew: true
      },
      {
        id: 10,
        name: 'Portable Bluetooth Speaker',
        price: 69.99,
        discountPrice: 59.99,
        image: 'assets/images/products/portable-speaker.jpg',
        rating: 4.2,
        reviewCount: 103,
        isSale: true
      },
      {
        id: 11,
        name: 'Wireless Earbuds',
        price: 129.99,
        image: 'assets/images/products/earbuds.jpg',
        rating: 4.7,
        reviewCount: 211
      },
      {
        id: 12,
        name: 'Smart Watch',
        price: 199.99,
        discountPrice: 179.99,
        image: 'assets/images/products/smartwatch.jpg',
        rating: 4.5,
        reviewCount: 175,
        isSale: true
      },
      {
        id: 13,
        name: 'Tablet with Keyboard',
        price: 399.99,
        image: 'assets/images/products/tablet.jpg',
        rating: 4.6,
        reviewCount: 98,
        isNew: true
      },
      {
        id: 14,
        name: 'Wireless Mouse',
        price: 49.99,
        image: 'assets/images/products/mouse.jpg',
        rating: 4.3,
        reviewCount: 87
      },
      {
        id: 15,
        name: 'External SSD Drive',
        price: 179.99,
        discountPrice: 159.99,
        image: 'assets/images/products/ssd.jpg',
        rating: 4.8,
        reviewCount: 64,
        isSale: true
      },
      {
        id: 16,
        name: 'Mechanical Keyboard',
        price: 149.99,
        image: 'assets/images/products/keyboard.jpg',
        rating: 4.4,
        reviewCount: 112
      }
    ];
  }
}
