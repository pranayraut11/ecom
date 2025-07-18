import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AdminLayoutComponent } from './components/admin-layout/admin-layout.component';
import { AdminDashboardComponent } from './pages/dashboard/admin-dashboard.component';
import { ThemeEditorComponent } from './pages/theme-editor/theme-editor.component';
import { ComponentEditorComponent } from './pages/component-editor/component-editor.component';
import { LayoutEditorComponent } from './pages/layout-editor/layout-editor.component';

const routes: Routes = [
  {
    path: '',
    component: AdminLayoutComponent,
    children: [
      {
        path: '',
        redirectTo: 'dashboard',
        pathMatch: 'full'
      },
      {
        path: 'dashboard',
        component: AdminDashboardComponent
      },
      {
        path: 'themes',
        component: ThemeEditorComponent
      },
      {
        path: 'components',
        component: ComponentEditorComponent
      },
      {
        path: 'layouts',
        component: LayoutEditorComponent
      }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class AdminRoutingModule { }
