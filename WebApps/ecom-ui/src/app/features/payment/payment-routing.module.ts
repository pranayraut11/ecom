import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { PaymentComponent } from './payment.component';
import { SavedCardsComponent } from './methods/saved-cards/saved-cards.component';
import { CardsComponent } from './methods/cards/cards.component';
import { NetBankingComponent } from './methods/net-banking/net-banking.component';
import { WalletsComponent } from './methods/wallets/wallets.component';
import { CashOnDeliveryComponent } from './methods/cash-on-delivery/cash-on-delivery.component';
import { UpiComponent } from './methods/upi/upi.component';

const routes: Routes = [
  {
    path: '',
    component: PaymentComponent,
    children: [
      {
        path: '',
        redirectTo: 'savedcards',
        pathMatch: 'full'
      },
      {
        path: 'savedcards',
        component: SavedCardsComponent
      },
      {
        path: 'cards',
        component: CardsComponent
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
        path: 'cash-on-delivery',
        component: CashOnDeliveryComponent
      },
      {
        path: 'upi',
        component: UpiComponent
      }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class PaymentRoutingModule { }