import { Component, OnInit, ViewChild } from '@angular/core';
import { Form, FormBuilder, FormControl, FormGroup, NgForm, Validators } from '@angular/forms';
import { timeout } from 'rxjs';
import { Media } from 'src/app/shared/models/media.module';
import { Price } from 'src/app/shared/models/price.model';
import { Product } from 'src/app/shared/models/product.model';
import { ProductRestService } from 'src/app/shared/services/rest-services/product-rest-service';
import { ProductBasicInfoComponent } from './components/product-basic-info/product-basic-info.component';

@Component({
  selector: 'app-catalog-create',
  templateUrl: './create.component.html',
  styleUrls: ['./create.component.css']
})
export class CreateProductComponent implements OnInit {

  images: File;
  constructor(private productRest: ProductRestService, private formBuilder: FormBuilder) { }
  isNextButtonDisabled = true;
  ngOnInit(): void {
    // this.submitForm = this.formBuilder.group({
    //   name: ['', Validators.required]
    // });
  }
  submitted = false;
  currentInput: string;
  onFileSelect($files: FileList) {
    console.log($files[0].name);
    this.images = $files[0];
  }
  
  onOutletLoaded(component: ProductBasicInfoComponent) {
    console.log(component)
    if (component instanceof ProductBasicInfoComponent) {
      component.formData.valueChanges.subscribe(res => {
        // Variable res holds the current value of the form
        const controls = component.formData.controls;
        this.isNextButtonDisabled = false;
        for (const name in controls) {
          if (controls[name].invalid) {
            this.isNextButtonDisabled = true;
          }
        }
       
      
      });
    }
  }
  getData(isFormValidFromChid:string){
    console.log("incoming "+isFormValidFromChid)
  }
  createProduct(form: NgForm) {

    console.log(form);
    const value = form.value.userData;
    console.log(value);
    const price = new Price(value.maxRetailPrice, value.discountedPrice, 0);
    const product = new Product(null, value.name, value.description, price, null, null);
    console.log(product);
    this.productRest.createProduct(product, this.images).subscribe(res => console.log(res));


  }

}
