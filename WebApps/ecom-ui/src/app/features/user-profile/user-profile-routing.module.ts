import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { UserProfileComponent } from './user-profile.component';
import { AddressComponent } from './components/components/address/address.component';
import { CreateAddressComponent } from './components/components/address/create-address/create-address.component';
import { UpdateProfileComponent } from './components/update-profile/update-profile.component';
import { NotificationComponent } from './components/notification/notification.component';
import { OrderListComponent } from '../order/pages/list/list.component';

const userProfileRoutes: Routes = [
  {
    path: '',
    component: UserProfileComponent,
    children: [
      {
        path: 'address', 
        component: AddressComponent, 
        children: [
          {
            path: '', // Default route, redirect to list
            redirectTo: 'list',
            pathMatch: 'full'
          },
          {
            path: 'list', 
            loadComponent: () => import('../user-profile/components/components/address/list-address/list-address.component').then(m => m.ListAddressComponent)
          },
          {
            path: 'create', 
            component: CreateAddressComponent
          },
          {
            path: 'update/:id', 
            component: CreateAddressComponent
          }
        ]
      },
      {
        path: 'my-profile', 
        component: UpdateProfileComponent
      },
      {
        path: 'myorders', 
        component: OrderListComponent
      },
      {
        path: 'notification', 
        component: NotificationComponent
      }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(userProfileRoutes)],
  exports: [RouterModule]
})
export class UserProfileRoutingModule { }
