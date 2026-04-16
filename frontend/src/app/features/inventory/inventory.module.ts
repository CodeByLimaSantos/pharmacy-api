import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { InventoryRoutingModule } from './inventory-routing.module';
import { InventoryListComponent } from './inventory-list.component';
import { InventoryFormComponent } from './inventory-form.component';

@NgModule({
  declarations: [
    InventoryListComponent,
    InventoryFormComponent
  ],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    InventoryRoutingModule
  ]
})
export class InventoryModule { }