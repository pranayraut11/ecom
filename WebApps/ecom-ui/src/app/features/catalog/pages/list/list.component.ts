import { Component, OnInit } from '@angular/core';
import { CartRestService } from 'src/app/shared/services/rest-services/cart-rest-service';
import { ProductRestService } from 'src/app/shared/services/rest-services/product-rest-service';
import { Product } from 'src/app/shared/models/product.model';
import { CartProduct } from 'src/app/shared/models/cart.product.model';
import { ActivatedRoute, Router } from '@angular/router';
import { CartService } from 'src/app/features/cart-service';
import { Page } from 'src/app/shared/models/Page.model';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-catalog-list',
  templateUrl: './list.component.html',
  styleUrls: ['./list.component.css'],
  standalone: true,
  imports: [
    CommonModule,
    FormsModule
  ]
})
export class ListComponent implements OnInit {


  products: Product[] = [];
  product: Product | null = null;
  
  // Filter and pagination state
  minPriceRange = 0;
  maxPriceRange = 5000;
  maxValue = 5000;
  currentPage = 0;
  pages = [1, 2, 3, 4, 5];
  pageResponse: Page = new Page(
    [], // content
    0,  // totalElements
    0,  // totalPages
    true, // first
    true, // last
    10,   // size
    0,    // number
    0,    // numberOfElements
    true  // empty
  );
  previousPagButton = 'page-item disabled';
  nextPagButton = 'page-item';

  selectedBrands: Set<string> = new Set();

  constructor(private actRouter: ActivatedRoute, private productRestService: ProductRestService, private cartRestService: CartRestService, private route: Router, private cartService: CartService) { }

  ngOnInit(): void {



    let id = this.actRouter.snapshot.paramMap.get("id");
    console.log("List category " + id)
    this.getProducts();
    this.disblePaginationButton();
  }

  disblePaginationButton() {
    if (this.pageResponse.first) {
      this.previousPagButton = 'page-item disabled';
      this.nextPagButton = 'page-item '
    } else if (this.pageResponse.last) {
      this.previousPagButton = 'page-item ';
      this.nextPagButton = 'page-item disabled'
    }
  }

  clearFilters(): void {
    this.minPriceRange = 0;
    this.maxPriceRange = this.maxValue;
    this.selectedBrands.clear();
    this.getProducts();
  }

  filterByBrand(event: any, brand: string): void {
    if (event.target.checked) {
      this.selectedBrands.add(brand);
    } else {
      this.selectedBrands.delete(brand);
    }
    this.getProducts();
  }

  getProducts(): void {
    let id = this.actRouter.snapshot.paramMap.get("id");
    const filters = {
      minPrice: this.minPriceRange,
      maxPrice: this.maxPriceRange,
      brands: Array.from(this.selectedBrands)
    };
    
    this.productRestService.getProducts(id, this.currentPage, filters).subscribe({
      next: (response: any) => {
        this.products = response.data || [];
        this.pageResponse = new Page(
          response.data || [],
          response.totalElements || 0,
          response.totalPages || 0,
          response.first || true,
          response.last || true,
          response.size || 10,
          response.number || 0,
          response.numberOfElements || 0,
          response.empty || true
        );
        this.products = this.pageResponse.content;
        this.disblePaginationButton();
      },
      error: (error) => {
        console.error('Error fetching products:', error);
        // TODO: Show error message to user
      }
    });
  }

  minRange(valueText: any) {
    this.minPriceRange = valueText;
  }

  maxRange(valueText: any) {
    this.maxPriceRange = valueText;
  }


  goToDetails(id: string) {
    console.log(id);
    this.route.navigate(['user/details/' + id]);
  }

  changePage(pageNumber: number) {
    this.currentPage = pageNumber;
    console.log("Page number " + this.currentPage)
    this.getProducts();
  }    sortProducts(event: any): void {
    const value = event.target.value;
    if (value === 'price-low') {
      this.products.sort((a, b) => (a.price?.price || 0) - (b.price?.price || 0));
    } else if (value === 'price-high') {
      this.products.sort((a, b) => (b.price?.price || 0) - (a.price?.price || 0));
    }
    // Add more sorting options as needed
  }

  addToCart(product: Product): void {
    this.cartService.addToCart(product).subscribe({
      next: () => {
        // Show success message or update cart count
        console.log('Product added to cart');
      },
      error: (error) => {
        console.error('Error adding to cart:', error);
      }
    });
  }

  addToWishlist(product: Product): void {
    // Implement wishlist functionality
    console.log('Add to wishlist:', product);
  }
  
  getProductImageUrl(product: any): string {
    // Check if product has images array with at least one item
    if (product.images && Array.isArray(product.images) && product.images.length > 0) {
      return product.images[0];
    }
    // Check if product has an image property
    if (product.image) {
      return product.image;
    }
    // Return placeholder
    return 'https://raw.githubusercontent.com/microsoft/vscode-copilot-release/main/sample-images/placeholder.jpg';
  }

  quickView(product: Product): void {
    // Navigate to product details
    console.log('Quick view for product:', product);
    this.route.navigate(['user/product', product.id]);
  }

  previousPage(): void {
    if (this.currentPage > 0) {
      this.currentPage--;
      this.getProducts();
    }
  }

  nextPage(): void {
    if (!this.pageResponse.last) {
      this.currentPage++;
      this.getProducts();
    }
  }

  goToPage(page: number): void {
    this.currentPage = page;
    this.getProducts();
  }
}
