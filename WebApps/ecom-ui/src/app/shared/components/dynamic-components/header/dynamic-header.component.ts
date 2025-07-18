import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { BaseComponent } from '../base.component';

@Component({
  selector: 'app-dynamic-header',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <header [ngStyle]="getStyleObject()" class="dynamic-header">
      <div class="container">
        <div class="row align-items-center">
          <!-- Logo -->
          <div class="col-md-2 col-sm-12 logo-container" *ngIf="getProperty('showLogo', true)">
            <a class="navbar-brand" href="#">
              <img [src]="getProperty('logoSrc', '../../../../assets/images/Logo.PNG')" class="logo-img" [alt]="getProperty('logoAlt', 'E-commerce Logo')">
            </a>
          </div>

          <!-- Search bar -->
          <div class="col-md-5 col-sm-12" *ngIf="getProperty('showSearch', true)">
            <div class="search-container">
              <div class="input-group"> 
                <input class="form-control search-input" type="search" placeholder="Search for products, brands and more" aria-label="Search">
                <div class="input-group-append">
                  <button class="btn search-btn" type="submit">
                    <i class="bi bi-search"></i> <span>Search</span>
                  </button>
                </div>
              </div>
            </div>
          </div>

          <!-- User Menu -->
          <div class="col-md-2 col-sm-12 user-menu" *ngIf="getProperty('showAccount', true)">
            <div class="dropdown">
              <button class="user-menu-link dropdown-toggle" id="profileDropdown">
                <i class="bi bi-person"></i>
                <span class="user-name">Account</span>
                <i class="bi bi-chevron-down"></i>
              </button>
            </div>
          </div>

          <!-- Cart -->
          <div class="col-md-1 col-sm-6 text-center" *ngIf="getProperty('showCart', true)">
            <a class="user-menu-link" routerLink="/user/cart/products">
              <i class="bi bi-cart"></i> Cart
            </a>
          </div>

          <!-- Additional Action -->
          <div class="col-md-2 col-sm-6 text-center">
            <a class="user-menu-link">
              <i class="bi bi-bank"></i> Seller
            </a>
          </div>
        </div>
      </div>
    </header>
  `,
  styles: [`
    .dynamic-header {
      background-color: var(--header-bg-color, #3f51b5);
      color: var(--header-text-color, #ffffff);
      box-shadow: var(--box-shadow);
      padding: var(--spacing-md) 0;
      position: sticky;
      top: 0;
      z-index: 1000;
      width: 100%;
      border-bottom: 1px solid rgba(255, 255, 255, 0.1);
    }
    
    .user-menu-link {
      color: var(--header-text-color, #ffffff);
      display: inline-flex;
      align-items: center;
      gap: 8px;
      text-decoration: none;
      font-weight: 500;
      cursor: pointer;
      padding: 10px 12px;
      border-radius: 4px;
      transition: background-color 0.2s;
      background-color: transparent;
      border: none;
      outline: none;
    }
    
    .logo-img {
      max-height: 42px;
      max-width: 100%;
      object-fit: contain;
      opacity: 0.9;
      transition: var(--transition-fast);
    }
  `]
})
export class DynamicHeaderComponent extends BaseComponent implements OnInit {
  constructor() {
    super();
  }

  ngOnInit(): void {
    // Additional initialization logic if needed
  }
}
