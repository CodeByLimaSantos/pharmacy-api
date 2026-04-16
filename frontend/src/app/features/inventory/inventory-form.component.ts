import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ApiService } from '../../../core/services/api.service';
import { ActivatedRoute, Router } from '@angular/router';

interface InventoryItem {
  id?: number;
  productName: string;
  quantity: number;
  minQuantity: number;
  location: string;
  // Add other fields as needed
}

@Component({
  selector: 'app-inventory-form',
  templateUrl: './inventory-form.component.html',
  styleUrls: ['./inventory-form.component.css']
})
export class InventoryFormComponent implements OnInit {
  inventoryForm: FormGroup;
  isEditMode = false;
  itemId: number | null = null;

  constructor(
    private fb: FormBuilder,
    private apiService: ApiService,
    private route: ActivatedRoute,
    private router: Router
  ) {
    this.inventoryForm = this.fb.group({
      productName: ['', Validators.required],
      quantity: ['', [Validators.required, Validators.min(0)]],
      minQuantity: ['', [Validators.required, Validators.min(0)]],
      location: ['', Validators.required]
      // Add other form controls as needed
    });
  }

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const id = params.get('id');
      if (id) {
        this.isEditMode = true;
        this.itemId = +id;
        this.loadItem(this.itemId);
      }
    });
  }

  loadItem(id: number): void {
    this.apiService.get(`inventory/${id}`).subscribe({
      next: (data: InventoryItem) => {
        this.inventoryForm.patchValue(data);
      },
      error: (error) => {
        console.error('Error loading inventory item:', error);
      }
    });
  }

  onSubmit(): void {
    if (this.inventoryForm.invalid) {
      return;
    }

    const formData = this.inventoryForm.value;

    if (this.isEditMode && this.itemId !== null) {
      // Update existing item
      this.apiService.put(`inventory/${this.itemId}`, formData).subscribe({
        next: () => {
          this.router.navigate(['/inventory/list']);
        },
        error: (error) => {
          console.error('Error updating inventory item:', error);
        }
      });
    } else {
      // Create new item
      this.apiService.post('inventory', formData).subscribe({
        next: () => {
          this.router.navigate(['/inventory/list']);
        },
        error: (error) => {
          console.error('Error creating inventory item:', error);
        }
      });
    }
  }

  onCancel(): void {
    this.router.navigate(['/inventory/list']);
  }
}