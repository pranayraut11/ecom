import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { BaseComponent } from '../base.component';

@Component({
  selector: 'app-dynamic-footer',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <footer [ngStyle]="getStyleObject()" class="dynamic-footer">
      <div class="container">
        <div class="row">
          <!-- Categories section -->
          <div class="col-md-3 col-sm-6" *ngIf="getProperty('showCategories', true)">
            <h5>Categories</h5>
            <ul class="footer-links">
              <li><a href="#">Electronics</a></li>
              <li><a href="#">Fashion</a></li>
              <li><a href="#">Home & Kitchen</a></li>
              <li><a href="#">Books</a></li>
              <li><a href="#">Beauty</a></li>
            </ul>
          </div>

          <!-- Information section -->
          <div class="col-md-3 col-sm-6">
            <h5>Information</h5>
            <ul class="footer-links">
              <li><a href="#">About Us</a></li>
              <li><a href="#">Contact Us</a></li>
              <li><a href="#">Privacy Policy</a></li>
              <li><a href="#">Terms & Conditions</a></li>
              <li><a href="#">FAQ</a></li>
            </ul>
          </div>

          <!-- Contact section -->
          <div class="col-md-3 col-sm-6" *ngIf="getProperty('showContactInfo', true)">
            <h5>Contact Us</h5>
            <address>
              <p><i class="bi bi-geo-alt"></i> 123 Main Street, City, Country</p>
              <p><i class="bi bi-telephone"></i> +1 234 567 890</p>
              <p><i class="bi bi-envelope"></i> support&#64;ecommstore.com</p>
            </address>
          </div>

          <!-- Newsletter section -->
          <div class="col-md-3 col-sm-6" *ngIf="getProperty('showNewsletter', true)">
            <h5>Newsletter</h5>
            <p>Subscribe to our newsletter for updates</p>
            <div class="input-group mb-3">
              <input type="email" class="form-control" placeholder="Your email" aria-label="Your email">
              <div class="input-group-append">
                <button class="btn btn-primary" type="button">Subscribe</button>
              </div>
            </div>
          </div>
        </div>

        <!-- Social links section -->
        <div class="row">
          <div class="col-md-12 social-links" *ngIf="getProperty('showSocialLinks', true)">
            <a href="#" class="social-icon"><i class="bi bi-facebook"></i></a>
            <a href="#" class="social-icon"><i class="bi bi-twitter"></i></a>
            <a href="#" class="social-icon"><i class="bi bi-instagram"></i></a>
            <a href="#" class="social-icon"><i class="bi bi-linkedin"></i></a>
            <a href="#" class="social-icon"><i class="bi bi-youtube"></i></a>
          </div>
        </div>

        <!-- Copyright section -->
        <div class="row">
          <div class="col-md-12 text-center copyright">
            <p>{{ getProperty('copyrightText', '© 2025 EcommStore. All rights reserved.') }}</p>
          </div>
        </div>
      </div>
    </footer>
  `,
  styles: [`
    .dynamic-footer {
      background-color: var(--footer-background-color, #f5f5f5);
      color: var(--footer-text-color, #333333);
      padding: 40px 0 20px;
    }
    .footer-links {
      list-style: none;
      padding-left: 0;
    }
    .footer-links li {
      margin-bottom: 10px;
    }
    .footer-links a {
      color: var(--footer-text-color, #333333);
      text-decoration: none;
    }
    .footer-links a:hover {
      color: var(--primary-color, #3f51b5);
      text-decoration: underline;
    }
    .social-links {
      display: flex;
      justify-content: center;
      margin: 20px 0;
    }
    .social-icon {
      display: inline-flex;
      align-items: center;
      justify-content: center;
      width: 40px;
      height: 40px;
      border-radius: 50%;
      background-color: var(--primary-color, #3f51b5);
      color: white;
      margin: 0 5px;
      text-decoration: none;
    }
    .social-icon:hover {
      background-color: var(--secondary-color, #f50057);
    }
    .copyright {
      margin-top: 20px;
      font-size: 0.9rem;
    }
  `]
})
export class DynamicFooterComponent extends BaseComponent {
}
