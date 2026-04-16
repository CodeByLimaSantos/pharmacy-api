import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthGuard } from './core/guards/auth.guard';

const routes: Routes = [
  {
    path: '',
    redirectTo: 'dashboard',
    pathMatch: 'full'
  },
  {
    path: 'dashboard',
    loadChildren: () => import('./features/dashboard/dashboard.module')
      .then(m => m.DashboardModule)
  },
  {
    path: 'inventory',
    loadChildren: () => import('./features/inventory/inventory.module')
      .then(m => m.InventoryModule)
  },
  {
    path: 'products',
    loadChildren: () => import('./features/products/products.module')
      .then(m => m.ProductsModule)
  },
  {
    path: 'customers',
    loadChildren: () => import('./features/customers/customers.module')
      .then(m => m.CustomersModule)
  },
  {
    path: 'suppliers',
    loadChildren: () => import('./features/suppliers/suppliers.module')
      .then(m => m.SuppliersModule)
  },
  {
    path: 'sales',
    loadChildren: () => import('./features/sales/sales.module')
      .then(m => m.SalesModule)
  },
  {
    path: 'financial',
    loadChildren: () => import('./features/financial/financial.module')
      .then(m => m.FinancialModule)
  },
  {
    path: '**',
    redirectTo: 'dashboard'
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {}