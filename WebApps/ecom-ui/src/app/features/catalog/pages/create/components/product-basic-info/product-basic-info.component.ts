import { Component, EventEmitter, Input, OnInit, Output, ViewChild } from '@angular/core';
import { Price } from 'src/app/shared/models/price.model';
import { Product } from 'src/app/shared/models/product.model';
import { ProductRestService } from 'src/app/shared/services/rest-services/product-rest-service';
import { Form, FormBuilder, FormControl, FormGroup, NgForm, Validators } from '@angular/forms';

@Component({
  selector: 'app-product-basic-info',
  templateUrl: './product-basic-info.component.html',
  styleUrls: ['./product-basic-info.component.css']
})
export class ProductBasicInfoComponent implements OnInit {

  ff: File;
  formData: FormGroup;
  submitted = false;
  constructor(private productRest: ProductRestService, private userData: FormBuilder) {
    this.formData = this.userData.group({
      name: new FormControl(),
      description: new FormControl(),
      images: new FormControl(),
      maxRetailPrice: new FormControl(),
      discountedPrice: new FormControl()
    });
  }

  ngOnInit(): void {
   
  }
  onFileSelect($files: FileList) {
    console.log($files[0].name);
    this.ff = $files[0];
  }

  createProduct(form: NgForm) {

    console.log(form);
    const value = form.value.userData;
    console.log(value);
    const price = new Price(value.maxRetailPrice, value.discountedPrice, 0);
    const product = new Product(null, value.name, value.description, price, null, null);
    console.log(product);
    this.productRest.createProduct(product, this.ff).subscribe(res => console.log(res));


  }
 
}
