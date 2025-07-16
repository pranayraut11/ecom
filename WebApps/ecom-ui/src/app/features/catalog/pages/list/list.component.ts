import { Component, OnInit } from '@angular/core';
import { CartRestService } from 'src/app/shared/services/rest-services/cart-rest-service';
import { ProductRestService } from 'src/app/shared/services/rest-services/product-rest-service';
import { Product } from 'src/app/shared/models/product.model';
import { CartProduct } from 'src/app/shared/models/cart.product.model';
import { ActivatedRoute, Router } from '@angular/router';
import { CartService } from 'src/app/features/cart/cart-service';
import { Page } from 'src/app/shared/models/Page.model';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-catalog-list',
  templateUrl: './list.component.html',
  styleUrls: ['./list.component.css'],
  standalone: true,
  imports: [CommonModule]
})
export class ListComponent implements OnInit {


  products: Product[];

  product: Product;

  minPriceRange = 0;
  maxPriceRange = 0;
  maxValue = 5000;
  currentPage = 0;
  pages = [1, 2,3,4,5];
  pageResponse: Page;
  previousPagButton = 'page-item disabled'
  nextPagButton = 'page-item disabled'

  constructor(private actRouter: ActivatedRoute, private productRestService: ProductRestService, private cartRestService: CartRestService, private route: Router, private cartService: CartService) { }

  ngOnInit(): void {



    let id = this.actRouter.snapshot.paramMap.get("id");
    console.log("List category " + id)
    this.getProducts();
    this.disblePaginationButton();
  }

  disblePaginationButton() {
    if (this.pageResponse.first) {
      this.previousPagButton = 'page-item disabled';
      this.nextPagButton = 'page-item '
    } else if (this.pageResponse.last) {
      this.previousPagButton = 'page-item ';
      this.nextPagButton = 'page-item disabled'
    }
  }

  getProducts() {
    this.productRestService.getProductList().subscribe((product: Page) => {

      console.log("Product list" + product.number);
      this.pageResponse = product;
      this.products = product.data;
      console.log(this.products);
      this.disblePaginationButton();
    });
  }

  minRange(valueText: any) {
    this.minPriceRange = valueText;
  }

  maxRange(valueText: any) {
    this.maxPriceRange = valueText;
  }


  goToDetails(id: string) {
    console.log(id);
    this.route.navigate(['user/details/' + id]);
  }

  changePage(pageNumber: number) {
    this.currentPage = pageNumber;
    console.log("Page number " + this.currentPage)
    this.getProducts();
  }
}
