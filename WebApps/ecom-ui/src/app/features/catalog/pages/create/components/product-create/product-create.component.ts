import { Component, OnInit } from '@angular/core';
import { Form, FormBuilder, FormControl, FormGroup, NgForm } from '@angular/forms';
import { Price } from 'src/app/shared/models/price.model';
import { Product } from 'src/app/shared/models/product.model';
import { ProductRestService } from 'src/app/shared/services/rest-services/product-rest-service';

@Component({
  selector: 'app-product-create',
  templateUrl: './product-create.component.html',
  styleUrls: ['./product-create.component.css']
})
export class ProductCreateComponent implements OnInit {



  

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
