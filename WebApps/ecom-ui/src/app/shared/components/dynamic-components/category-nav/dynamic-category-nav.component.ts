import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { BaseComponent } from '../base.component';

interface Category {
  name: string;
  url: string;
  icon?: string;
}

@Component({
  selector: 'app-dynamic-category-nav',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <div class="category-nav-container" [ngStyle]="getStyleObject()">
      <div class="container">
        <div class="row">
          <div class="col-12">
            <div class="category-nav">
              <div class="category-item" *ngFor="let category of categories">
                <a [routerLink]="category.url" class="category-link">
                  <i *ngIf="showIcons && category.icon" class="bi" [ngClass]="'bi-' + category.icon"></i>
                  <span class="category-name">{{ category.name }}</span>
                </a>
              </div>
              <div class="category-item" *ngIf="showAll">
                <a routerLink="/categories" class="category-link view-all">
                  <span class="category-name">View All</span>
                  <i class="bi bi-arrow-right"></i>
                </a>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .category-nav-container {
      background-color: var(--background-color, #ffffff);
      padding: 1rem 0;
      margin-bottom: 1.5rem;
      box-shadow: 0 2px 5px rgba(0,0,0,0.1);
    }
    
    .category-nav {
      display: flex;
      flex-wrap: nowrap;
      overflow-x: auto;
      gap: 1.5rem;
      padding: 0.5rem 0;
      -webkit-overflow-scrolling: touch;
    }
    
    .category-nav::-webkit-scrollbar {
      height: 4px;
    }
    
    .category-nav::-webkit-scrollbar-track {
      background: #f1f1f1;
    }
    
    .category-nav::-webkit-scrollbar-thumb {
      background: var(--primary-color, #3f51b5);
      border-radius: 10px;
    }
    
    .category-item {
      flex: 0 0 auto;
    }
    
    .category-link {
      display: flex;
      flex-direction: column;
      align-items: center;
      text-decoration: none;
      color: var(--text-color, #333333);
      padding: 0.5rem 1rem;
      border-radius: 4px;
      transition: all 0.2s ease;
    }
    
    .category-link:hover {
      color: var(--primary-color, #3f51b5);
      background-color: rgba(63, 81, 181, 0.1);
    }
    
    .category-link i {
      font-size: 1.5rem;
      margin-bottom: 0.5rem;
    }
    
    .category-name {
      font-size: 0.9rem;
      font-weight: 500;
      white-space: nowrap;
    }
    
    .view-all {
      flex-direction: row;
      gap: 0.5rem;
    }
    
    @media (max-width: 768px) {
      .category-nav {
        gap: 1rem;
      }
      
      .category-link {
        padding: 0.5rem;
      }
    }
  `]
})
export class DynamicCategoryNavComponent extends BaseComponent implements OnInit {
  categories: Category[] = [];
  showIcons: boolean = true;
  showAll: boolean = true;
  maxCategories: number = 8;

  ngOnInit(): void {
    // Get properties from component data
    this.showIcons = this.getProperty('showIcons', true);
    this.showAll = this.getProperty('showAll', true);
    this.maxCategories = this.getProperty('maxCategories', 8);
    
    // Demo categories - in a real app, these would come from a service
    this.categories = [
      { name: 'Electronics', url: '/category/electronics', icon: 'laptop' },
      { name: 'Fashion', url: '/category/fashion', icon: 'bag' },
      { name: 'Home & Kitchen', url: '/category/home-kitchen', icon: 'house' },
      { name: 'Books', url: '/category/books', icon: 'book' },
      { name: 'Beauty', url: '/category/beauty', icon: 'gem' },
      { name: 'Sports', url: '/category/sports', icon: 'bicycle' },
      { name: 'Toys', url: '/category/toys', icon: 'controller' },
      { name: 'Grocery', url: '/category/grocery', icon: 'cart' }
    ].slice(0, this.maxCategories);
  }
}
