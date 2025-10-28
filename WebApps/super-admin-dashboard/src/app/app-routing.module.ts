import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LayoutComponent } from './layout/layout.component';

const routes: Routes = [
   // { path: 'login', loadChildren: () => import('./auth/auth.module').then(m => m.AuthModule) },
    // Add register and forgot-password modules as needed
    {
        path: '',
        component: LayoutComponent,
        children: [
            { path: 'tenants', loadChildren: () => import('./features/tenants/tenants.module').then(m => m.TenantsModule) },
            // { path: 'users', loadChildren: () => import('./features/users/users.module').then(m => m.UsersModule) },
            // { path: 'billing', loadChildren: () => import('./features/billing/billing.module').then(m => m.BillingModule) },
            // { path: 'analytics', loadChildren: () => import('./features/analytics/analytics.module').then(m => m.AnalyticsModule) },
            // { path: 'settings', loadChildren: () => import('./features/settings/settings.module').then(m => m.SettingsModule) },
            // { path: 'security', loadChildren: () => import('./features/security/security.module').then(m => m.SecurityModule) },
            // { path: 'support', loadChildren: () => import('./features/support/support.module').then(m => m.SupportModule) },
            // { path: '', redirectTo: 'tenants', pathMatch: 'full' }
        ]
    }
];

@NgModule({
    imports: [RouterModule.forRoot(routes)],
    exports: [RouterModule]
})
export class AppRoutingModule { }
