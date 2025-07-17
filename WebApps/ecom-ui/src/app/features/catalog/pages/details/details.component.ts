import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { CommunicationService } from 'src/app/core/services/communication-service';
import { CartService } from 'src/app/features/cart-service';
import { CartProduct } from 'src/app/shared/models/cart.product.model';
import { Product } from 'src/app/shared/models/product.model';
import { CartRestService } from 'src/app/shared/services/rest-services/cart-rest-service';
import { ProductRestService } from 'src/app/shared/services/rest-services/product-rest-service';

@Component({
  selector: 'app-details',
  templateUrl: './details.component.html',
  styleUrls: ['./details.component.css'],
  standalone: true,
  imports: [CommonModule, RouterModule]
})
export class DetailsComponent implements OnInit {
  product: Product;
  selectedImageIndex: number = 0;
  selectedColorIndex: number = 0;
  deliveryDate: Date = new Date(Date.now() + 7 * 24 * 60 * 60 * 1000); // 7 days from now
  isLoading: boolean = true;

  constructor(
    private actRouter: ActivatedRoute,
    private catalogRestService: ProductRestService,
    private cartRestService: CartRestService,
    private router: Router,
    private cartService: CartService,
    private communicationService: CommunicationService
  ) { }

  ngOnInit(): void {
    let id = this.actRouter.snapshot.paramMap.get("id");
    if (id) {
      this.getProduct(id);
    } else {
      console.error('No product ID provided');
      this.router.navigate(['/']);
    }
  }

  changeImage(imageSrc: string, index: number): void {
    this.selectedImageIndex = index;
    const mainProductImage = document.getElementById('main_product_image') as HTMLImageElement;
    if (mainProductImage && imageSrc) {
      mainProductImage.src = imageSrc;
    }
  }
  
  getMainImageUrl(): string {
    if (this.product && this.product.images && Array.isArray(this.product.images) && this.product.images.length > 0) {
      return this.product.images[this.selectedImageIndex] || this.product.images[0];
    }
    return this.getPlaceholderImage();
  }
  
  getPlaceholderImage(): string {
    return 'https://raw.githubusercontent.com/microsoft/vscode-copilot-release/main/sample-images/placeholder.jpg';
  }

  selectColor(index: number): void {
    this.selectedColorIndex = index;
    // Additional logic for changing product variant could go here
  }

  getProduct(id: string) {
    this.isLoading = true;
    this.catalogRestService.getProduct(id).subscribe({
      next: (response) => {
        console.log("Product:", response);
        this.product = response;
        this.isLoading = false;
      },
      error: (error) => {
        console.error("Error fetching product:", error);
        this.isLoading = false;
        // Could add error handling/messaging here
      }
    });
  }

  addToCart(product: Product) {
    this.cartService.addToCart(product).subscribe({
      next: () => {
        console.log('Product added to cart successfully');
        // Show success notification
      },
      error: (error) => {
        console.error('Error adding product to cart:', error);
        // Show error notification
      }
    });
  }

  gotoBuyProduct(product: Product) {
    this.communicationService.addProductId(product.id);
    this.router.navigate(['/bynow', { productId: product.id }]);
  }
}
