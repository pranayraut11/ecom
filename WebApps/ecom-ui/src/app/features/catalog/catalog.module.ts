import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ListComponent } from './pages/list/list.component';
import { DetailsComponent } from './pages/details/details.component';
import { CreateComponent } from './pages/create/create.component';
import { TableListComponent } from './pages/table-list/table-list.component';
import { BasicInfoComponent } from './create/components/basic-info/basic-info.component';
import { ProductBasicInfoComponent } from './pages/create/components/product-template/product-template.component';
import { ProductDetailedInfoComponent } from './pages/create/components/product-create/product-create.component';
import { CategoryComponent } from './pages/category/category.component';



@NgModule({
  declarations: [
    ListComponent,
    DetailsComponent,
    CreateComponent,
    TableListComponent,
    BasicInfoComponent,
    ProductBasicInfoComponent,
    ProductDetailedInfoComponent,
    CategoryComponent
  ],
  imports: [
    CommonModule
  ]
})
export class CatalogModule { }
