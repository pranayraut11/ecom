import { Component, OnInit, AfterViewInit } from '@angular/core';
import { Router } from '@angular/router';
import { CategoryRestService } from 'src/app/shared/services/rest-services/category-rest-service';
import { CommonModule } from '@angular/common';
import { Category } from 'src/app/shared/models/Category.model';
declare var bootstrap: any;

@Component({
  selector: 'app-category',
  templateUrl: './category.component.html',
  styleUrls: ['./category.component.css'],
  standalone: true,
  imports: [CommonModule]
})
export class CategoryComponent implements OnInit, AfterViewInit {
  categories: Category[];
  private carousel: any;

  constructor(private categoryService: CategoryRestService, private route: Router) { }

  currentSlide = 0;
  slides = [0, 1, 2]; // Indices for our three slides

  ngOnInit(): void {
    this.categoryService.getCategories().subscribe((categories: any[]) => {
      console.log('Raw categories data:', JSON.stringify(categories));
      
      // Process categories to ensure each subcategory has an imageUrl
      this.categories = categories.map(category => {
        console.log(`Processing category: ${category.name}`);
        
        if (category.subCategories) {
          category.subCategories = category.subCategories.map(subCategory => {
            console.log(`Processing subcategory: ${subCategory.name}`);
            console.log(`  - imageUrl: ${subCategory.imageUrl}`);
            console.log(`  - url: ${subCategory.url}`);
            
            // If subCategory has url but no imageUrl, use url as imageUrl
            if (subCategory.url && !subCategory.imageUrl) {
              console.log(`  - Using url as imageUrl for ${subCategory.name}`);
              subCategory.imageUrl = subCategory.url;
            }
            // If neither exists, provide a fallback
            if (!subCategory.imageUrl && !subCategory.url) {
              console.log(`  - Using fallback image for ${subCategory.name}`);
              subCategory.imageUrl = 'https://raw.githubusercontent.com/microsoft/vscode-copilot-release/main/sample-images/placeholder.jpg';
            }
            return subCategory;
          });
        }
        return category;
      });
    });
  }

  ngAfterViewInit(): void {
    // Start auto-sliding
    setInterval(() => {
      this.nextSlide();
    }, 5000);
  }

  // Navigate to previous slide
  prevSlide(): void {
    this.currentSlide = this.currentSlide === 0 ? 
      this.slides.length - 1 : this.currentSlide - 1;
  }

  // Navigate to next slide
  nextSlide(): void {
    this.currentSlide = this.currentSlide === this.slides.length - 1 ? 
      0 : this.currentSlide + 1;
  }

  // Check if a slide is active
  isActive(index: number): boolean {
    return this.currentSlide === index;
  }

  getList(category: string) {
    console.log(category);
    this.route.navigate(['user/list/' + category]);
  }

  getImageUrl(subCategory: any): string {
    // First check for imageUrl property
    if (subCategory.imageUrl) {
      return subCategory.imageUrl;
    }
    // Then check for url property
    if (subCategory.url) {
      return subCategory.url;
    }
    // Finally return a placeholder
    return 'https://raw.githubusercontent.com/microsoft/vscode-copilot-release/main/sample-images/placeholder.jpg';
  }

}
