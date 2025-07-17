import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ShoppingCartComponent } from './shopping-cart.component';
import { ShoppingCartRoutingModule } from './shopping-cart-routing.module';
import { RouterModule } from '@angular/router';

@NgModule({
  declarations: [
    // No declarations needed as components are standalone
  ],
  imports: [
    CommonModule,
    ShoppingCartRoutingModule,
    RouterModule,
    
    // Import standalone components
    ShoppingCartComponent
  ]
})
export class ShoppingCartModule { }
