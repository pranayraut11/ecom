import { Component, OnInit } from '@angular/core';
import { Product } from 'src/app/shared/models/product.model';
import { ProductRestService } from 'src/app/shared/services/rest-services/product-rest-service';

@Component({
  selector: 'app-table-list',
  templateUrl: './table-list.component.html',
  styleUrls: ['./table-list.component.css']
})
export class TableListComponent implements OnInit {

  products: Product[];
 
  constructor(private productRestService: ProductRestService) { }

  ngOnInit(): void {
    this.productRestService.getProductList().subscribe((product: Product[]) => {
      console.log(product);
      product.map((pr) => {
        console.log(pr);
        if (pr.images) {
          pr.imageUrl = pr.id + "/" + pr.images[0];

        } else {
          pr.imageUrl = "asd";
        }
      }
      );
      this.products = product;
      console.log(this.products);
    });
  }

  deleteProduct(productId : any){
    console.log("product id "+productId)
   const ids :  string[] = [];
      ids.push(productId);
      this.productRestService.deleteProduct(ids).subscribe((res=>{
          console.log(res);
          this.ngOnInit();
      }));
  }
}
