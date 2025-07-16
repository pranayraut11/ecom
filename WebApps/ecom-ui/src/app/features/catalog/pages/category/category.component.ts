import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CategoryRestService } from 'src/app/shared/services/rest-services/category-rest-service';
import { CommonModule } from '@angular/common';
import { Category } from 'src/app/shared/models/Category.model';

@Component({
  selector: 'app-category',
  templateUrl: './category.component.html',
  styleUrls: ['./category.component.css'],
  standalone: true,
  imports: [CommonModule]
})
export class CategoryComponent implements OnInit {

 
  categories: Category[];

  constructor(private categoryService: CategoryRestService, private route: Router) { }

  ngOnInit(): void {
    this.categoryService.getCategories().subscribe((category: any[]) => {
      console.log("Category :"+category);
      this.categories = category;
      console.log(this.categories);
    });

  }

  
  getList(category: string) {
    console.log(category);
    this.route.navigate(['user/list/'+category]);
  }

}
