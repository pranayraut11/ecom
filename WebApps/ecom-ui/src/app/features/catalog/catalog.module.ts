import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ListComponent } from './pages/list/list.component';
import { DetailsComponent } from './pages/details/details.component';
import { CreateComponent } from './pages/create/create.component';
import { TableListComponent } from './pages/table-list/table-list.component';
import { BasicInfoComponent } from './create/components/basic-info/basic-info.component';
import { ProductBasicInfoComponent } from './pages/create/components/product-basic-info/product-basic-info.component';
import { ProductDetailedInfoComponent } from './pages/create/components/product-detailed-info/product-detailed-info.component';



@NgModule({
  declarations: [
    ListComponent,
    DetailsComponent,
    CreateComponent,
    TableListComponent,
    BasicInfoComponent,
    ProductBasicInfoComponent,
    ProductDetailedInfoComponent
  ],
  imports: [
    CommonModule
  ]
})
export class CatalogModule { }
