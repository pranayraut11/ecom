import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { AppComponent } from './app.component';
import { HeaderComponent } from './shared/components/header/header.component'; 
import { CatalogListComponent } from './features/catalog/pages/list/list.component';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { CartListComponent } from './features/cart/pages/list/list.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { CreateOrderComponent } from './features/order/pages/create/create.component';
import { CreateProductComponent } from './features/catalog/pages/create/create.component';
import { HomeComponent } from './features/layout/customer/pages/home/home.component';
import { FooterComponent } from './shared/components/footer/footer.component'; 
import { FilterComponent } from './features/layout/customer/components/filter/filter.component';
import { LoginComponent } from './shared/components/login/login.component';
import { UserRegistrationComponent } from './shared/components/user-registration/user-registration.component';
import { AuthInterceptorService } from './core/core/auth/Auth-interceptor-service';
import { RolesDirective } from './core/directives/roles.directive';
import { AppRoutingModule } from './app-routing.module';
import { LoadingSpinnerComponent } from './shared/components/loading-spinner/loading-spinner.component';
import { SellerHomeComponent } from './features/layout/seller/pages/home/home.component';
import { TableListComponent } from './features/catalog/pages/table-list/table-list.component';
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
    RolesDirective,
    LoadingSpinnerComponent,
    SellerHomeComponent,
    TableListComponent
  ],
  imports: [
    BrowserModule, HttpClientModule, FormsModule, AppRoutingModule,ReactiveFormsModule
  ],
  providers: [{provide:HTTP_INTERCEPTORS,useClass:AuthInterceptorService,multi:true}],
  bootstrap: [AppComponent]
})
export class AppModule { }
