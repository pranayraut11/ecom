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
import { OrderListComponent } from './features/order/pages/list/list.component';
import { ProfileComponent } from './features/profile/profile.component';
import { ProfileMenuComponent } from './features/profile/components/profile-menu/profile-menu.component';
import { CreateAddressComponent } from './features/profile/components/address/create-address/create-address.component';
import { UpdateProfileComponent } from './features/profile/components/update-profile/update-profile.component';
import { ListAddressComponent } from './features/profile/components/address/list-address/list-address.component';
import { AddressComponent } from './features/profile/components/address/address.component';
import { CartComponent } from './features/cart/cart.component';
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
import { AppCatalogRoutingModule } from './features/catalog/app-catalog-routing.module';
import { ProductTemplateComponent } from './features/catalog/pages/create/components/product-template/product-template.component';
import { ProductCreateComponent } from './features/catalog/pages/create/components/product-create/product-create.component';
import { NotificationComponent } from './features/profile/components/notification/notification.component';
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
    TableListComponent,
    OrderListComponent,
    ProfileComponent,
    ProfileMenuComponent,
    CreateAddressComponent,
    UpdateProfileComponent,
    ListAddressComponent,
    AddressComponent,
    CartComponent,
    PaymentComponent,
    UpiComponent,
    CardsComponent,
    WalletsComponent,
    CashOnDeliveryComponent,
    NetBankingComponent,
    SavedCardsComponent,
    MenubarComponent,
    SearchComponent,
    SellerHeaderComponent,
    SellerMenuComponent,
    BackCreateButtonComponent,
    ProductTemplateComponent,
    ProductCreateComponent,
    NotificationComponent
  ],
  imports: [
    BrowserModule, HttpClientModule, FormsModule, AppRoutingModule,ReactiveFormsModule, AppCatalogRoutingModule,
  ],
  providers: [{provide:HTTP_INTERCEPTORS,useClass:AuthInterceptorService,multi:true}],
  bootstrap: [AppComponent]
})
export class AppModule { }
