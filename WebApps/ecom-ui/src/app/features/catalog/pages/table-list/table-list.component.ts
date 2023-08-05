import { Component, OnInit } from '@angular/core';
import { Product } from 'src/app/shared/models/product.model';
import { ProductRestService } from 'src/app/shared/services/rest-services/product-rest-service';

@Component({
  selector: 'app-table-list',
  templateUrl: './table-list.component.html',
  styleUrls: ['./table-list.component.css']
})
export class TableListComponent implements OnInit {

  createPath:string = '/seller/create/select';
  products: Product[];
 
  constructor(private productRestService: ProductRestService) { }

  ngOnInit(): void {
    this.productRestService.getProductList().subscribe((product: Product[]) => {
      console.log(product);
     
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
