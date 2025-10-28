import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LayoutComponent } from './layout.component';
import { RouterModule } from '@angular/router';
import { HeaderComponent } from './header/header.component';
import { SidebarComponent } from './sidebar/sidebar.component';
import { FooterComponent } from './footer/footer.component';


@NgModule({
  declarations: [
    
  ],
  imports: [
    CommonModule,
    RouterModule,
    LayoutComponent,
    HeaderComponent,
    SidebarComponent,
    FooterComponent
  ],
  exports: [LayoutComponent]
})
export class LayoutModule {}