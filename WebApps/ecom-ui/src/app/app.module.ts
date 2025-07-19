import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ToastrModule } from 'ngx-toastr';
import { AppComponent } from './app.component';
import { ProductListComponent } from './features/product-list/product-list.component';
import { HeaderComponent } from './shared/components/header/header.component';
import { ListComponent } from './features/catalog/pages/list/list.component';
import { CartListComponent } from './features/shopping-cart/pages/cart-page/list.component';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { CreateOrderComponent } from './features/order/pages/create/create.component';
import { CreateProductComponent } from './features/catalog/pages/create/create.component';
import { HomeComponent } from './layout/customer-layout/pages/home/home.component';
import { FooterComponent } from './shared/components/footer/footer.component';
import { FilterComponent } from './layout/customer-layout/components/filter/filter.component';
import { LoginComponent } from './shared/components/login/login.component';
import { UserRegistrationComponent } from './shared/components/user-registration/user-registration.component';
import { AuthInterceptorService } from './core/auth/interceptors/auth-interceptor.service';
import { RolesDirective } from './core/directives/roles.directive';
import { AppRoutingModule } from './app-routing.module';
import { LoadingSpinnerComponent } from './shared/components/loading-spinner/loading-spinner.component';
import { SellerHomeComponent } from './layout/seller-layout/pages/home/home.component';
import { OrderListComponent } from './features/order/pages/list/list.component';
import { UserProfileComponent } from './features/user-profile/user-profile.component';
import { ProfileMenuComponent } from './features/user-profile/components/profile-menu/profile-menu.component';
import { CreateAddressComponent } from './features/user-profile/components/address/create-address/create-address.component';
import { UpdateProfileComponent } from './features/user-profile/components/update-profile/update-profile.component';
import { AddressComponent } from './features/user-profile/components/address/address.component';
import { ShoppingCartComponent } from './features/shopping-cart/shopping-cart.component';
import { PaymentComponent } from './features/payment/payment.component';
import { UpiComponent } from './features/payment/methods/upi/upi.component';
import { CardsComponent } from './features/payment/methods/cards/cards.component';
import { WalletsComponent } from './features/payment/methods/wallets/wallets.component';
import { CashOnDeliveryComponent } from './features/payment/methods/cash-on-delivery/cash-on-delivery.component';
import { NetBankingComponent } from './features/payment/methods/net-banking/net-banking.component';
import { SavedCardsComponent } from './features/payment/methods/saved-cards/saved-cards.component';
import { MenubarComponent } from './shared/components/menubar/menubar.component';
import { SearchComponent } from './shared/components/search/search.component';
import { SellerHeaderComponent } from './features/layout/seller/component/seller-header/seller-header.component';
import { SellerMenuComponent } from './features/layout/seller/component/seller-menu/seller-menu.component';
import { BackCreateButtonComponent } from './shared/components/back-create-button/back-create-button.component';
import { ProductTemplateComponent } from './features/catalog/pages/create/components/product-template/product-template.component';
import { ProductCreateComponent } from './features/catalog/pages/create/components/product-create/product-create.component';
import { NotificationComponent } from './features/user-profile/components/notification/notification.component';
import { LoginPopupComponent } from './shared/components/login/login-popup/login-popup.component';
import { DashboardComponent } from './features/dashboard/dashboard.component';
import { AppSellerRoutingModule } from './layout/seller-layout/app-seller-routing-module';
import { CategoryComponent } from './features/catalog/pages/category/category.component';
import { DetailsComponent } from './features/catalog/pages/details/details.component';
import { OrderSuccessComponent } from './features/order/order-success/order-success.component';
import { RouterModule } from '@angular/router';
import { SearchResultsComponent } from './features/search/search-results.component';
import { TableListComponent } from './features/catalog/pages/table-list/table-list.component';

@NgModule({
  declarations: [
    // Regular components (not standalone)
    CreateProductComponent,
    CreateOrderComponent,
    FilterComponent,
    SellerHomeComponent,
    OrderListComponent,

    SellerHeaderComponent,
    SellerMenuComponent,
    ProductTemplateComponent,
    
    DashboardComponent,
    

    // Add all standalone components here
      ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,

    HttpClientModule, 
    FormsModule, 
    AppRoutingModule,
    ReactiveFormsModule,
    AppSellerRoutingModule,
    RouterModule,
    CommonModule,
    FooterComponent ,
    AppComponent,
    HeaderComponent,
    // FooterComponent is standalone and should be imported, not declared
    LoginComponent,
    UserRegistrationComponent,
    RolesDirective,
    LoadingSpinnerComponent,
    MenubarComponent,
    SearchComponent,
    BackCreateButtonComponent,
    LoginPopupComponent,
    ListComponent,
    CartListComponent,
    UpiComponent,
    CardsComponent,
    WalletsComponent,
    CashOnDeliveryComponent,
    NetBankingComponent,
    SavedCardsComponent,
   
    HomeComponent,
    UpdateProfileComponent,
    ProfileMenuComponent,
    AddressComponent,
    ShoppingCartComponent,
    PaymentComponent,
    TableListComponent,
    CreateAddressComponent,
    CategoryComponent,
    ProductCreateComponent,
    NotificationComponent,
    DetailsComponent,
    OrderSuccessComponent,
    SearchResultsComponent,
    ProductListComponent
// Import the standalone FooterComponent here
  ],
  providers: [
    {provide: HTTP_INTERCEPTORS, useClass: AuthInterceptorService, multi: true}
  ]
})
export class AppModule { }
