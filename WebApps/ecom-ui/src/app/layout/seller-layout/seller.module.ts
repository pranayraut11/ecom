import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { HttpClientModule } from '@angular/common/http';

// Import the routing module
import { AppSellerRoutingModule } from './app-seller-routing-module';

// Import components
import { SellerHomeComponent } from './pages/home/home.component';
import { SellerHeaderComponent } from './component/seller-header/seller-header.component';
import { SellerMenuComponent } from './component/seller-menu/seller-menu.component';

// Import shared module
import { SharedModule } from '../../../shared/shared.module';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    RouterModule,
    HttpClientModule,
    SharedModule,
    AppSellerRoutingModule,
    // Import standalone components
    SellerHomeComponent,
    SellerHeaderComponent,
    SellerMenuComponent
  ]
})
export class SellerModule { }
