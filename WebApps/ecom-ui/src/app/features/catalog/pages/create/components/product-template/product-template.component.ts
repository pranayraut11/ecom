import { Component, EventEmitter, Input, OnInit, Output, ViewChild } from '@angular/core';
import { Price } from 'src/app/shared/models/price.model';
import { Product } from 'src/app/shared/models/product.model';
import { ProductRestService } from 'src/app/shared/services/rest-services/product-rest-service';
import { Form, FormBuilder, FormControl, FormGroup, NgForm, Validators } from '@angular/forms';
import { Router } from '@angular/router';

@Component({
  selector: 'app-product-template',
  templateUrl: './product-template.component.html',
  styleUrls: ['./product-template.component.css']
})
export class ProductTemplateComponent implements OnInit {

  formData: FormGroup;
  productList: any;
  product: Product;
  productId: string;
  currentProduct: string = "";
  selectedProduct: Product;
  isProductSelected = false;

  constructor(private formGroupbuilder: FormBuilder, private catalogRestService: ProductRestService, private productService: ProductRestService,private router: Router) {
    this.formData = this.formGroupbuilder.group({
      productSearch: new FormControl(),
      maxRetailPrice: new FormControl(),
      discountedPrice: new FormControl(),
      quantity: new FormControl()
    });
  }

  ngOnInit(): void {
    this.updateProductListOnTextChange();
  }

  gotoCreateProduct(){
    this.router.navigate(['seller/create/create-new']);
  }
  updateProductListOnTextChange() {
    this.formData.get('productSearch').valueChanges.subscribe(response => {
      this.productList = null;
      if (response === '') {
        this.isProductSelected = false;
      } else {
        this.getProductListOnSearch(response);
      }
    })
  }

  getProductListOnSearch(text: string) {
    this.productService.searchProduct(text).subscribe(response => {
      this.productList = response;
    })
  }

  getProduct(id: string) {
    this.catalogRestService.getProduct(id).subscribe(response => {
      this.product = response;
      this.isProductSelected = true;
    });

  }

  onProductChanged(currentProduct: string) {
    if (currentProduct === '') {
      this.isProductSelected = false;
    } else {
      this.getProduct(currentProduct);
      this.selectedProduct = this.product;
    }
  }

  changeImage(element) {

    //var main_prodcut_image = document.getElementById('main_product_image');
    // main_prodcut_image.src = element.src;


  }

 
}
