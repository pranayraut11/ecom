import { CommonModule } from "@angular/common";
import { NgModule } from "@angular/core";
import { RouterModule, Routes } from "@angular/router";
import { SellerHomeComponent } from "./pages/home/home.component";
import { SellerMenuComponent } from "./component/seller-menu/seller-menu.component";
import { NotificationComponent } from "../../profile/components/notification/notification.component";
import { DashboardComponent } from "../../dashboard/dashboard.component";
import { CreateProductComponent } from "../../catalog/pages/create/create.component";
import { ProductTemplateComponent } from "../../catalog/pages/create/components/product-template/product-template.component";
import { ProductCreateComponent } from "../../catalog/pages/create/components/product-create/product-create.component";
import { TableListComponent } from "../../catalog/pages/table-list/table-list.component";
import { UpdateProfileComponent } from "../../profile/components/update-profile/update-profile.component";
import { AddressComponent } from "../../profile/components/address/address.component";
import { CreateAddressComponent } from "../../profile/components/address/create-address/create-address.component";


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
                        component: ProductCreateComponent
                    }
                ]
            },
            {
                path: 'dashboard',
                component: DashboardComponent
            },
            {
                path: 'product',
                component: TableListComponent,
            },
            {
                path: 'notification', component: NotificationComponent
            },
            {
                path: 'profile', children: [
                    {
                        path: 'personal-info', component: UpdateProfileComponent
                    },
                    {
                        path: 'address', component:AddressComponent, children: [
                            {
                                path: 'create', component: CreateAddressComponent
                            },
                            {
                                path: 'list', loadComponent: () => import('../../../../features/profile/components/address/list-address/list-address.component').then(m => m.ListAddressComponent)
                            }
                        ]
                    },

                ]
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
export class AppSellerRoutingModule { }