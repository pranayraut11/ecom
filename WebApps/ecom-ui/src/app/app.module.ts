import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppComponent } from './app.component';
import { HeaderComponent } from './header/header.component';
import { CatalogListComponent } from './features/catalog/pages/list/list.component';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { CartListComponent } from './features/cart/pages/list/list.component';
import { FormsModule } from '@angular/forms';
import { CreateOrderComponent } from './features/order/pages/create/create.component';
import { CreateProductComponent } from './features/catalog/pages/create/create.component';
import { HomeComponent } from './features/layout/customer/pages/home/home.component';
import { FooterComponent } from './features/layout/customer/components/footer/footer.component';
import { FilterComponent } from './features/layout/customer/components/filter/filter.component';
import { LoginComponent } from './shared/components/login/login.component';
import { UserRegistrationComponent } from './shared/components/user-registration/user-registration.component';
import { AuthInterceptorService } from './core/core/auth/Auth-interceptor-service';
import { RolesDirective } from './core/directives/roles.directive';
import { AppRoutingModule } from './app-routing.module';


@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    CatalogListComponent,
    CreateProductComponent,
    CartListComponent,
    CreateOrderComponent,
    CreateProductComponent,
    HomeComponent,
    FooterComponent,
    FilterComponent,
    LoginComponent,
    UserRegistrationComponent,
    RolesDirective
  ],
  imports: [
    BrowserModule, HttpClientModule, FormsModule, AppRoutingModule
  ],
  providers: [{provide:HTTP_INTERCEPTORS,useClass:AuthInterceptorService,multi:true}],
  bootstrap: [AppComponent]
})
export class AppModule { }
