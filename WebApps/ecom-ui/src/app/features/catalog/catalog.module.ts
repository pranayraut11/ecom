import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ListComponent } from './pages/list/list.component';
import { DetailsComponent } from './pages/details/details.component';
import { CreateComponent } from './pages/create/create.component';
import { TableListComponent } from './pages/table-list/table-list.component';



@NgModule({
  declarations: [
    ListComponent,
    DetailsComponent,
    CreateComponent,
    TableListComponent
  ],
  imports: [
    CommonModule
  ]
})
export class CatalogModule { }
