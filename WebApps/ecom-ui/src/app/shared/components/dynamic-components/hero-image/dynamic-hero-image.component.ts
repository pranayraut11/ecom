import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { BaseComponent } from '../base.component';

@Component({
  selector: 'app-dynamic-hero-image',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <div class="hero-section" [ngStyle]="getStyleObject()">
      <div class="container">
        <div class="hero-content" [ngClass]="getProperty('contentPosition', 'center')">
          <h1 class="hero-title">{{ getProperty('title', 'Welcome to Our Store') }}</h1>
          <p class="hero-subtitle">{{ getProperty('subtitle', 'Discover amazing products at great prices') }}</p>
          <a [routerLink]="getProperty('ctaLink', '/products')" class="hero-cta-button">
            {{ getProperty('ctaText', 'Shop Now') }}
          </a>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .hero-section {
      background-size: cover;
      background-position: center;
      background-repeat: no-repeat;
      height: 500px;
      display: flex;
      align-items: center;
      position: relative;
      color: white;
      text-align: center;
      overflow: hidden;
    }
    
    .hero-section::before {
      content: "";
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      background-color: rgba(0, 0, 0, 0.4);
      z-index: 1;
    }
    
    .container {
      position: relative;
      z-index: 2;
      width: 100%;
    }
    
    .hero-content {
      max-width: 600px;
      margin: 0 auto;
      padding: 20px;
    }
    
    .hero-content.left {
      margin-left: 0;
      text-align: left;
    }
    
    .hero-content.right {
      margin-right: 0;
      text-align: right;
      margin-left: auto;
    }
    
    .hero-title {
      font-size: 3rem;
      font-weight: 700;
      margin-bottom: 1rem;
    }
    
    .hero-subtitle {
      font-size: 1.5rem;
      margin-bottom: 2rem;
    }
    
    .hero-cta-button {
      display: inline-block;
      padding: 12px 30px;
      background-color: var(--primary-color, #3f51b5);
      color: white;
      text-decoration: none;
      border-radius: 5px;
      font-weight: 600;
      transition: all 0.3s ease;
    }
    
    .hero-cta-button:hover {
      background-color: var(--secondary-color, #f50057);
      transform: translateY(-2px);
    }
    
    @media (max-width: 768px) {
      .hero-section {
        height: 400px;
      }
      
      .hero-title {
        font-size: 2rem;
      }
      
      .hero-subtitle {
        font-size: 1.2rem;
      }
    }
  `]
})
export class DynamicHeroImageComponent extends BaseComponent {
  ngOnInit() {
    // Set background image from properties
    if (this.component?.properties?.imageSrc) {
      const styles = this.getStyleObject();
      styles['backgroundImage'] = `url('${this.component.properties.imageSrc}')`;
    }
  }
}
