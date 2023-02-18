import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Product } from 'src/app/shared/models/product.model';
import { ProductRestService } from 'src/app/shared/services/rest-services/product-rest-service';

@Component({
  selector: 'app-details',
  templateUrl: './details.component.html',
  styleUrls: ['./details.component.css']
})
export class DetailsComponent implements OnInit {

  constructor(private actRouter : ActivatedRoute,private catalogRestService: ProductRestService) { }
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
}
