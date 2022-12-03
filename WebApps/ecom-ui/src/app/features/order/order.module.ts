import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SummaryComponent } from './pages/summary/summary.component';
import { CreateComponent } from './pages/create/create.component';



@NgModule({
  declarations: [
    SummaryComponent,
    CreateComponent
  ],
  imports: [
    CommonModule
  ]
})
export class OrderModule { }
