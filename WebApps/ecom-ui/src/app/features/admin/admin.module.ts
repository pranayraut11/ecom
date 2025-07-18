import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { DragDropModule } from '@angular/cdk/drag-drop';
import { AdminRoutingModule } from './admin-routing.module';
import { AdminDashboardComponent } from './pages/dashboard/admin-dashboard.component';
import { AdminHeaderComponent } from './components/admin-header/admin-header.component';
import { AdminSidebarComponent } from './components/admin-sidebar/admin-sidebar.component';
import { AdminLayoutComponent } from './components/admin-layout/admin-layout.component';
import { ThemeEditorComponent } from './pages/theme-editor/theme-editor.component';
import { ComponentEditorComponent } from './pages/component-editor/component-editor.component';
import { LayoutEditorComponent } from './pages/layout-editor/layout-editor.component';

@NgModule({
  declarations: [
    AdminDashboardComponent,
    AdminHeaderComponent,
    AdminSidebarComponent,
    AdminLayoutComponent,
    ThemeEditorComponent,
    ComponentEditorComponent,
    LayoutEditorComponent
  ],
  imports: [
    CommonModule,
    RouterModule,
    FormsModule,
    ReactiveFormsModule,
    HttpClientModule,
    AdminRoutingModule,
    DragDropModule
  ],
  exports: [
    AdminLayoutComponent,
    AdminHeaderComponent,
    AdminSidebarComponent
  ]
})
export class AdminModule { }
