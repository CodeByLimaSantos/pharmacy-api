import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ApiService } from '../../../core/services/api.service';
import { ActivatedRoute, Router } from '@angular/router';

interface DashboardEntity {
  id?: number;
  name: string;
  description: string;
  // Add other fields as needed for dashboard entity
}

@Component({
  selector: 'app-dashboard-form',
  templateUrl: './dashboard-form.component.html',
  styleUrls: ['./dashboard-form.component.css']
})
export class DashboardFormComponent implements OnInit {
  dashboardForm: FormGroup;
  isEditMode = false;
  entityId: number | null = null;

  constructor(
    private fb: FormBuilder,
    private apiService: ApiService,
    private route: ActivatedRoute,
    private router: Router
  ) {
    this.dashboardForm = this.fb.group({
      name: ['', Validators.required],
      description: ['', Validators.required]
      // Add other form controls as needed
    });
  }

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const id = params.get('id');
      if (id) {
        this.isEditMode = true;
        this.entityId = +id;
        this.loadEntity(this.entityId);
      }
    });
  }

  loadEntity(id: number): void {
    // Assuming we have an API endpoint to get dashboard entity by id
    // This is a placeholder - adjust according to actual API
    this.apiService.get(`dashboard/${id}`).subscribe({
      next: (data: DashboardEntity) => {
        this.dashboardForm.patchValue(data);
      },
      error: (error) => {
        console.error('Error loading dashboard entity:', error);
      }
    });
  }

  onSubmit(): void {
    if (this.dashboardForm.invalid) {
      return;
    }

    const formData = this.dashboardForm.value;

    if (this.isEditMode && this.entityId !== null) {
      // Update existing entity
      this.apiService.put(`dashboard/${this.entityId}`, formData).subscribe({
        next: () => {
          this.router.navigate(['/dashboard/list']);
        },
        error: (error) => {
          console.error('Error updating dashboard entity:', error);
        }
      });
    } else {
      // Create new entity
      this.apiService.post('dashboard', formData).subscribe({
        next: () => {
          this.router.navigate(['/dashboard/list']);
        },
        error: (error) => {
          console.error('Error creating dashboard entity:', error);
        }
      });
    }
  }

  onCancel(): void {
    this.router.navigate(['/dashboard/list']);
  }
}