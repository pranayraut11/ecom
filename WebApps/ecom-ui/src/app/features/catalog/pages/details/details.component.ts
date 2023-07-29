import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CartProduct } from 'src/app/shared/models/cart.product.model';
import { Product } from 'src/app/shared/models/product.model';
import { CartRestService } from 'src/app/shared/services/rest-services/cart-rest-service';
import { ProductRestService } from 'src/app/shared/services/rest-services/product-rest-service';

@Component({
  selector: 'app-details',
  templateUrl: './details.component.html',
  styleUrls: ['./details.component.css']
})
export class DetailsComponent implements OnInit {

  constructor(private actRouter : ActivatedRoute,private catalogRestService: ProductRestService,private cartRestService: CartRestService,private router : Router) { }
  product : Product;
  ngOnInit(): void {
    let id = this.actRouter.snapshot.paramMap.get("id");
    this.getProduct(id);
  }

  changeImage(element) {

    //var main_prodcut_image = document.getElementById('main_product_image');
    // main_prodcut_image.src = element.src;


  }

  getProduct(id : string) {
    this.catalogRestService.getProduct(id).subscribe((response)=>{
      this.product = response;
    });

  }

  addToCart(product: Product) {
    let cartProduct = new CartProduct("", product.id, product.name, product.images[0], product.price.price, product.price.discountedPrice, product.price.discount, 1);
    console.log(product);
    this.cartRestService.addToCart(cartProduct).subscribe(response => {
      console.log(response);
    });;
  }

  gotoBuyProduct(product: Product){
      this.router.navigate(["/user/cart/address"]);
  }
}
