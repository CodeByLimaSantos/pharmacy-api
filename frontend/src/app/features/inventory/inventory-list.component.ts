import { Component, OnInit } from '@angular/core';
import { ApiService } from '../../../core/services/api.service';

interface InventoryItem {
  id: number;
  productName: string;
  quantity: number;
  minQuantity: number;
  location: string;
  // Add other fields as needed
}

@Component({
  selector: 'app-inventory-list',
  templateUrl: './inventory-list.component.html',
  styleUrls: ['./inventory-list.component.css']
})
export class InventoryListComponent implements OnInit {
  inventoryItems: InventoryItem[] = [];
  displayedColumns: string[] = ['productName', 'quantity', 'minQuantity', 'location', 'actions'];

  constructor(private apiService: ApiService) {}

  ngOnInit(): void {
    this.loadInventory();
  }

  loadInventory(): void {
    this.apiService.get('inventory').subscribe({
      next: (data: InventoryItem[]) => {
        this.inventoryItems = data;
      },
      error: (error) => {
        console.error('Error loading inventory:', error);
      }
    });
  }

  deleteItem(id: number): void {
    if (confirm('Are you sure you want to delete this inventory item?')) {
      this.apiService.delete(`inventory/${id}`).subscribe({
        next: () => {
          this.loadInventory();
        },
        error: (error) => {
          console.error('Error deleting inventory item:', error);
        }
      });
    }
  }
}