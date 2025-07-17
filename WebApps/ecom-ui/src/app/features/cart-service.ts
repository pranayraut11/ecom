import { Injectable } from '@angular/core';
import { CartProduct } from '../shared/models/cart.product.model';
import { CartRestService } from '../shared/services/rest-services/cart-rest-service';
import { Observable, of } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class CartService {
  private cartProducts: CartProduct[] = [];

  constructor(private cartRestService: CartRestService) {}

  addToCart(product: any): Observable<any> {
    // Modify this to fit your application's needs
    const cartProduct = {
      productId: product.id,
      quantity: 1
    };
    
    return this.cartRestService.addToCart(cartProduct as CartProduct);
  }

  getCartProducts(): Observable<CartProduct[]> {
    return this.cartRestService.getMyCartProducts();
  }
}
