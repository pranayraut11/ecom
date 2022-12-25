import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, NgForm, Validators } from '@angular/forms';
import { timeout } from 'rxjs';
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

  ff: File;
  
  constructor(private productRest: ProductRestService,private formBuilder: FormBuilder) { }

  ngOnInit(): void {
    // this.submitForm = this.formBuilder.group({
    //   name: ['', Validators.required]
    // });
  }
  submitted = false;
  currentInput: string;
  onFileSelect($files: FileList) {
    console.log($files[0].name);
    this.ff = $files[0];
  }
  createProduct(form: NgForm) {
  
    console.log(form);
    const value = form.value;
    const price = new Price(value.maxRetailPrice, value.discountedPrice, 0);
    const product = new Product(null, value.name, value.description, price, null);
    console.log(product);
    this.productRest.createProduct(product, this.ff).subscribe(res => console.log(res)) ;
    
  
  }

}
