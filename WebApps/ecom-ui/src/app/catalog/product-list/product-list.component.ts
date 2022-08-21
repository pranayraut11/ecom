import { Component, OnInit } from '@angular/core';
import { Cart } from 'src/app/shared/models/cart';
import { CartRestService } from 'src/app/shared/services/rest-services/cart-rest-service';
import { ProductRestService } from 'src/app/shared/services/rest-services/product-rest-service';
import { Product } from 'src/app/shared/models/product.model';
import { CartProduct } from 'src/app/shared/models/cart.product.model';

@Component({
  selector: 'app-product-list',
  templateUrl: './product-list.component.html',
  styleUrls: ['./product-list.component.css']
})
export class ProductListComponent implements OnInit {
  products: Product[];
  constructor(private productRestService: ProductRestService, private cartRestService: CartRestService) { }

  ngOnInit(): void {
    this.productRestService.getProductList().subscribe((product: Product[]) => {
      console.log(product)
      this.products = product;
    });

  }

  addToCart(product: Product) {
    let cartProduct = new CartProduct("",product.id, product.name, product.images[0].url, product.price.price, product.price.discountedPrice, product.price.discount, 1);
    console.log(product);
    this.cartRestService.addToCart(cartProduct).subscribe(response=>{
      console.log(response);
  });;
  }

}
