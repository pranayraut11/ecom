import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CommunicationService } from 'src/app/core/services/communication-service';
import { CartService } from 'src/app/features/cart/cart-service';
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

  constructor(private actRouter : ActivatedRoute,private catalogRestService: ProductRestService,private cartRestService: CartRestService,private router : Router,private cartService: CartService,private communicationService : CommunicationService) { }
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
      console.log("Product :"+response);
      this.product = response;
    });

  }

  addToCart(product: Product) {
    this.cartService.addToCart(product);
  }

  gotoBuyProduct(product: Product){
      this.communicationService.addProductId(product.id);
      this.router.navigate(['/bynow',{productId:product.id}]);
  }
}
