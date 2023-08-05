import { Component, OnInit } from '@angular/core';
import { Form, FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { Product } from 'src/app/shared/models/product.model';
import { ProductRestService } from 'src/app/shared/services/rest-services/product-rest-service';

@Component({
  selector: 'app-product-detailed-info',
  templateUrl: './product-detailed-info.component.html',
  styleUrls: ['./product-detailed-info.component.css']
})
export class ProductDetailedInfoComponent implements OnInit {

  formData: FormGroup;
  productList: any;
  product: Product;
  productId: string;
  currentProduct: string = "";
  selectedProduct: Product;
  isProductSelected = false;

  constructor(private formGroupbuilder: FormBuilder, private catalogRestService: ProductRestService, private productService: ProductRestService) {
    this.formData = this.formGroupbuilder.group({
      productSearch: new FormControl()
    });
  }

  ngOnInit(): void {
    this.updateProductListOnTextChange();
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
