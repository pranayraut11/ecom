import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppComponent } from './app.component';
import { HeaderComponent } from './header/header.component';
import { CatalogComponent } from './catalog/catalog.component';
import { ProductListComponent } from './catalog/product-list/product-list.component';
import { ProductComponent } from './catalog/product/product.component';
import { HttpClientModule } from '@angular/common/http';
import { CartComponent } from './cart/cart.component'
import { RouterModule, Routes } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { OrderComponent } from './order/order.component';
import { CreateProductComponent } from './seller/create-product/create-product.component';
import { HomeComponent } from './features/layout/customer/pages/home/home.component';
import { FooterComponent } from './features/layout/customer/components/footer/footer.component';
import { FilterComponent } from './features/layout/customer/components/filter/filter.component';
import { LoginComponent } from './shared/components/login/login.component';
import { UserRegistrationComponent } from './shared/components/user-registration/user-registration.component';

const appsRoutes: Routes = [
  { path: 'cart', component: CartComponent }, 
  { path: 'myorders', component: OrderComponent },
  { path: 'product/create', component: CreateProductComponent },
  { path: 'auth/register', component: UserRegistrationComponent },
  { path: '', component: CatalogComponent }
];

@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    CatalogComponent,
    ProductListComponent,
    ProductComponent,
    CartComponent,
    OrderComponent,
    CreateProductComponent,
    HomeComponent,
    FooterComponent,
    FilterComponent,
    LoginComponent,
    UserRegistrationComponent
  ],
  imports: [
    BrowserModule, HttpClientModule, RouterModule.forRoot(appsRoutes), FormsModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
