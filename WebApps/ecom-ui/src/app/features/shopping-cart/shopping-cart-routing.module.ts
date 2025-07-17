import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ShoppingCartComponent } from './shopping-cart.component';

const routes: Routes = [
  {
    path: '',
    component: ShoppingCartComponent,
    children: [
      {
        path: '',
        loadComponent: () => import('./pages/cart-page/list.component').then(m => m.CartListComponent),
      }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ShoppingCartRoutingModule { }
