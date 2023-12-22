import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CartService } from 'src/app/features/cart/cart-service';
import { Category } from 'src/app/shared/models/category.model';
import { Product } from 'src/app/shared/models/product.model';
import { CartRestService } from 'src/app/shared/services/rest-services/cart-rest-service';
import { CategoryRestService } from 'src/app/shared/services/rest-services/category-rest-service';
import { ProductRestService } from 'src/app/shared/services/rest-services/product-rest-service';

@Component({
  selector: 'app-category',
  templateUrl: './category.component.html',
  styleUrls: ['./category.component.css']
})
export class CategoryComponent implements OnInit {

 
  category: Category[];

  constructor(private categoryService: CategoryRestService, private route: Router) { }

  ngOnInit(): void {
    this.categoryService.getCategory().subscribe((category: any[]) => {
      console.log(category);
      this.category = category;
      console.log(this.category);
    });

  }

  
  getList(category: string) {
    console.log(category);
    this.route.navigate(['user/list/'+category]);
  }

}
