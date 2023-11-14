import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SummaryComponent } from './pages/summary/summary.component';
import { CreateComponent } from './pages/create/create.component';
import { ListComponent } from './pages/list/list.component';
import { SuccessComponent } from './success/success.component';
import { OrderSuccessComponent } from './order-success/order-success.component';



@NgModule({
  declarations: [
    SummaryComponent,
    CreateComponent,
    ListComponent,
    SuccessComponent,
    OrderSuccessComponent
  ],
  imports: [
    CommonModule
  ]
})
export class OrderModule { }
