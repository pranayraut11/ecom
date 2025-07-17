import { Component, NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { UserRegistrationComponent } from './shared/components/user-registration/user-registration.component';
import { HomeComponent } from './layout/customer-layout/pages/home/home.component';
import { TableListComponent } from './features/catalog/pages/table-list/table-list.component';
import { OrderListComponent } from './features/order/pages/list/list.component';
import { PaymentComponent } from './features/payment/payment.component';
import { UpiComponent } from './features/payment/methods/upi/upi.component';
import { SavedCardsComponent } from './features/payment/methods/saved-cards/saved-cards.component';
import { NetBankingComponent } from './features/payment/methods/net-banking/net-banking.component';
import { WalletsComponent } from './features/payment/methods/wallets/wallets.component';
import { CardsComponent } from './features/payment/methods/cards/cards.component';
import { CashOnDeliveryComponent } from './features/payment/methods/cash-on-delivery/cash-on-delivery.component';
import { DetailsComponent } from './features/catalog/pages/details/details.component';
import { OrderSuccessComponent } from './features/order/order-success/order-success.component';
import { LoginComponent } from './shared/components/login/login.component';
import { LoginPopupComponent } from './shared/components/login/login-popup/login-popup.component';
import { CategoryComponent } from './features/catalog/pages/category/category.component';

const appsRoutes: Routes = [
  { path: 'create', redirectTo: 'seller/create', pathMatch: 'full' },
  { path: '', redirectTo: 'user/list', pathMatch: 'full' },
  { path: 'home', redirectTo: 'user/list', pathMatch: 'prefix' },
  { path: 'usergome', component: HomeComponent },
  { path: 'debug', loadComponent: () => import('./features/debug/debug.component').then(c => c.DebugComponent) },
  { path: 'bynow', redirectTo: 'user/cart', pathMatch: 'prefix' },
  {
    path: 'user', component: HomeComponent, children: [
      {
        path: 'list',
        component: CategoryComponent,
      },
      {
        path: 'list/:id',
        loadComponent: () => import('./features/catalog/pages/list/list.component').then(c => c.ListComponent),
      },
      {
        path: 'login',
        component: LoginComponent,
      },
      {
        path: 'login-popup',
        component: LoginPopupComponent,
      },
      {
        path: 'register',
        component: UserRegistrationComponent,
      },
      {
        path: 'details/:id',
        component: DetailsComponent
      },
      {
        path: 'cart',
        loadChildren: () => import('./features/shopping-cart/shopping-cart.module').then(m => m.ShoppingCartModule)
      },
      {
        path: 'payment',
        loadChildren: () => import('./features/payment/payment.module').then(m => m.PaymentModule)
      },
      {
        path: 'myorders',
        component: OrderListComponent,
      },
      {
        path: 'order-successfull',
        loadComponent: () => import('./features/order/order-success/order-success.component').then(m => m.OrderSuccessComponent),
      },
      {
        path: 'profile',
        loadChildren: () => import('./features/user-profile/user-profile.module').then(m => m.UserProfileModule)
      },
      {
        path: 'addresses',
        redirectTo: 'profile/address',
        pathMatch: 'full'
      },
      {
        path: 'checkout-address',
        loadComponent: () => import('./features/user-profile/components/components/address/list-address/list-address.component').then(m => m.ListAddressComponent),
        data: { isCheckout: true }
      },
      {
        path: 'checkout-add-address',
        loadComponent: () => import('./features/user-profile/components/components/address/create-address/create-address.component').then(m => m.CreateAddressComponent),
        data: { isCheckout: true }
      }
    ]
  }
];

@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    RouterModule.forRoot(appsRoutes)
  ],
  exports: [RouterModule]
})
export class AppRoutingModule { }
