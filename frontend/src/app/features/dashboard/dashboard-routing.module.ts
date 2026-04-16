import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { DashboardListComponent } from './dashboard-list.component';
import { DashboardFormComponent } from './dashboard-form.component';

const routes: Routes = [
  { path: '', redirectTo: 'list', pathMatch: 'full' },
  { path: 'list', component: DashboardListComponent },
  { path: 'create', component: DashboardFormComponent },
  { path: 'edit/:id', component: DashboardFormComponent }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class DashboardRoutingModule { }