import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { TenantCreateComponent } from './tenant-create/tenant-create.component';

const routes: Routes = [
    { path: 'create', component: TenantCreateComponent },
    { path: '', redirectTo: 'list', pathMatch: 'full' }
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class TenantsRoutingModule { }