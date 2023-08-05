import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { SellerHomeComponent } from '../layout/seller/pages/home/home.component';
import { CreateProductComponent } from './pages/create/create.component';
import { SellerMenuComponent } from '../layout/seller/component/seller-menu/seller-menu.component';
import { TableListComponent } from './pages/table-list/table-list.component';
import { ProductBasicInfoComponent } from './pages/create/components/product-basic-info/product-basic-info.component';
import { ProductDetailedInfoComponent } from './pages/create/components/product-detailed-info/product-detailed-info.component';

const appsRoutes: Routes = [
  {
    path: 'seller', component: SellerHomeComponent, children: [
      {
        path: 'create',
        component: CreateProductComponent, children: [
          {
            path: 'select',
            component: ProductDetailedInfoComponent
          },
          {
            path: 'create-new',
            component: ProductBasicInfoComponent
          }
        ]

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
  }
]

@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    RouterModule.forRoot(appsRoutes)
  ]
})
export class AppCatalogRoutingModule { }
