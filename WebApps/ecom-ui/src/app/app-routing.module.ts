import { Component, NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { CartListComponent } from './features/cart/pages/list/list.component';
import { CreateOrderComponent } from './features/order/pages/create/create.component';
import { CreateProductComponent } from './features/catalog/pages/create/create.component';
import { UserRegistrationComponent } from './shared/components/user-registration/user-registration.component';
import { CatalogListComponent } from './features/catalog/pages/list/list.component';
import { SellerHomeComponent } from './features/layout/seller/pages/home/home.component';
import { HomeComponent } from './features/layout/customer/pages/home/home.component';
import { TableListComponent } from './features/catalog/pages/table-list/table-list.component';
import { OrderListComponent } from './features/order/pages/list/list.component';
import { ProfileComponent } from './features/profile/profile.component';
import { CreateAddressComponent } from './features/profile/components/address/create-address/create-address.component';
import { UpdateProfileComponent } from './features/profile/components/update-profile/update-profile.component';
import { ListAddressComponent } from './features/profile/components/address/list-address/list-address.component';
import { AddressComponent } from './features/profile/components/address/address.component';
import { CartComponent } from './features/cart/cart.component';
import { PaymentComponent } from './features/payment/payment.component';
import { UpiComponent } from './features/payment/methods/upi/upi.component';
import { SavedCardsComponent } from './features/payment/methods/saved-cards/saved-cards.component';
import { NetBankingComponent } from './features/payment/methods/net-banking/net-banking.component';
import { WalletsComponent } from './features/payment/methods/wallets/wallets.component';
import { CardsComponent } from './features/payment/methods/cards/cards.component';
import { CashOnDeliveryComponent } from './features/payment/methods/cash-on-delivery/cash-on-delivery.component';
import { DetailsComponent } from './features/catalog/pages/details/details.component';
import { SellerMenuComponent } from './features/layout/seller/component/seller-menu/seller-menu.component';

const appsRoutes: Routes = [
  // { path: 'cart', component: CartListComponent }, 
  //{ path: 'myorders', component: OrderListComponent },
  { path: 'create', redirectTo: 'seller/create', pathMatch: 'full' },
  // { path: 'auth/register', component: UserRegistrationComponent },
  { path: '', redirectTo: 'user/list', pathMatch: 'full' },
  { path: 'home', redirectTo: 'user/list', pathMatch: 'prefix' },
  { path: 'seller', redirectTo: 'seller/dashboard', pathMatch: 'prefix' },
  {
    path: 'user', component: HomeComponent, children: [
      {
        path: 'list',
        component: CatalogListComponent,

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
        component: CartComponent, children: [
          {
            path: 'products',
            component: CartListComponent
          },
          {
            path: 'address',
            component: ListAddressComponent
          },
          {
            path: 'payment',
            component: PaymentComponent,
            children: [
              {
                path: 'upi',
                component: UpiComponent
              },
              {
                path: 'savedcards',
                component: SavedCardsComponent
              },
              {
                path: 'net-banking',
                component: NetBankingComponent
              },
              {
                path: 'wallets',
                component: WalletsComponent
              },
              {
                path: 'cards',
                component: CardsComponent
              },
              {
                path: 'cash-on-delivery',
                component: CashOnDeliveryComponent
              }
            ]
          }
        ]

      },
      {
        path: 'myorders',
        component: OrderListComponent,

      },
      {
        path: 'profile',
        component: ProfileComponent, children: [
          {
            path: 'address', component: AddressComponent, children: [
              {
                path: 'list', component: ListAddressComponent
              },
              {
                path: 'create/:id', component: CreateAddressComponent
              }
            ]
          },

          {
            path: 'my-profile', component: UpdateProfileComponent
          },

          {
            path: 'myorders', component: OrderListComponent
          }
        ]

      }
    ]
  },
  {
    path: 'seller', component: SellerHomeComponent, children: [

      {
        path: 'create',
        component: CreateProductComponent,

      },
      {
        path: 'dashboard',
        component: SellerMenuComponent
      },
      {
        path: 'product',
        component: TableListComponent,
      }
    ]
  },

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
