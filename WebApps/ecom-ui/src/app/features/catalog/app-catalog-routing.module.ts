import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { SellerHomeComponent } from '../layout/seller/pages/home/home.component';
import { CreateProductComponent } from './pages/create/create.component';
import { SellerMenuComponent } from '../layout/seller/component/seller-menu/seller-menu.component';
import { TableListComponent } from './pages/table-list/table-list.component';
import { ProductTemplateComponent } from './pages/create/components/product-template/product-template.component';
import { ProductCreateComponent } from './pages/create/components/product-create/product-create.component';

const appsRoutes: Routes = [
  {
    path: 'seller', component: SellerHomeComponent, children: [
      {
        path: 'create',
        component: CreateProductComponent, children: [
          {
            path: 'select',
            component: ProductTemplateComponent
          },
          {
            path: 'create-new',
            component:   ProductCreateComponent
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
