import { Component, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { Media } from 'src/app/shared/models/media.module';
import { Price } from 'src/app/shared/models/price.model';
import { Product } from 'src/app/shared/models/product.model';
import { ProductRestService } from 'src/app/shared/services/rest-services/product-rest-service';

@Component({
  selector: 'app-catalog-create',
  templateUrl: './create.component.html',
  styleUrls: ['./create.component.css']
})
export class CreateProductComponent implements OnInit {

 

  constructor(private productRest : ProductRestService ) { }

  ngOnInit(): void {
  }

  createProduct(form :NgForm){
    const value = form.value;
    const price = new Price(value.maxRetailPrice,value.discountedPrice,0);
    const media = new Media("",value.images);
    const mediaList : Media[] = [] ;
    mediaList.push(media);
    const product = new Product(null,value.name,value.description,price,mediaList);
    console.log(product);
    this.productRest.createProduct(product).subscribe(res=>console.log(res));
  }

}
