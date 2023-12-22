import { Component, OnInit } from '@angular/core';
import { CartRestService } from 'src/app/shared/services/rest-services/cart-rest-service';
import { ProductRestService } from 'src/app/shared/services/rest-services/product-rest-service';
import { Product } from 'src/app/shared/models/product.model';
import { CartProduct } from 'src/app/shared/models/cart.product.model';
import { ActivatedRoute, Router } from '@angular/router';
import { CartService } from 'src/app/features/cart/cart-service';

@Component({
  selector: 'app-catalog-list',
  templateUrl: './list.component.html',
  styleUrls: ['./list.component.css']
})
export class CatalogListComponent implements OnInit {


  products: Product[];

  product : Product;


  constructor(private actRouter : ActivatedRoute,private productRestService: ProductRestService, private cartRestService: CartRestService,private route: Router,private cartService: CartService) { }

  ngOnInit(): void {
    let id = this.actRouter.snapshot.paramMap.get("id");
    console.log("List category "+id)
    this.productRestService.getProductList().subscribe((product: any[]) => {
      console.log(product);
      this.products = product;
      console.log(this.products);
    });
  }

  
  goToDetails(id: string) {
    console.log(id);
    this.route.navigate(['user/details/'+id]);
  }

}
