import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SummaryComponent } from './pages/summary/summary.component';
import { CreateComponent } from './pages/create/create.component';
import { ListComponent } from './pages/list/list.component';



@NgModule({
  declarations: [
    SummaryComponent,
    CreateComponent,
    ListComponent
  ],
  imports: [
    CommonModule
  ]
})
export class OrderModule { }
