import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { CartListComponent } from './features/cart/pages/list/list.component';
import { CreateOrderComponent } from './features/order/pages/create/create.component';
import { CreateProductComponent } from './features/catalog/pages/create/create.component';
import { UserRegistrationComponent } from './shared/components/user-registration/user-registration.component';
import { CatalogListComponent } from './features/catalog/pages/list/list.component';

const appsRoutes: Routes = [
  { path: 'cart', component: CartListComponent }, 
  { path: 'myorders', component: CreateOrderComponent },
  { path: 'product/create', component: CreateProductComponent },
  { path: 'auth/register', component: UserRegistrationComponent },
  { path: '', component: CatalogListComponent },
  { path: 'home', component: CatalogListComponent }
];


@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    RouterModule.forRoot(appsRoutes)
  ],
  exports:[RouterModule]
})
export class AppRoutingModule { }
