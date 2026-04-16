import { Component, OnInit } from '@angular/core';
import { ApiService } from '../../../core/services/api.service';

interface DashboardStats {
  totalCustomers: number;
  totalProducts: number;
  totalSales: number;
  totalSuppliers: number;
  totalFinancial: number;
}

@Component({
  selector: 'app-dashboard-list',
  templateUrl: './dashboard-list.component.html',
  styleUrls: ['./dashboard-list.component.css']
})
export class DashboardListComponent implements OnInit {
  stats: DashboardStats = {
    totalCustomers: 0,
    totalProducts: 0,
    totalSales: 0,
    totalSuppliers: 0,
    totalFinancial: 0
  };

  constructor(private apiService: ApiService) {}

  ngOnInit(): void {
    this.loadDashboardStats();
  }

  loadDashboardStats(): void {
    // Assuming API endpoints for stats
    this.apiService.get('dashboard/stats').subscribe({
      next: (data: any) => {
        this.stats = {
          totalCustomers: data.totalCustomers || 0,
          totalProducts: data.totalProducts || 0,
          totalSales: data.totalSales || 0,
          totalSuppliers: data.totalSuppliers || 0,
          totalFinancial: data.totalFinancial || 0
        };
      },
      error: (error) => {
        console.error('Error loading dashboard stats:', error);
      }
    });
  }
}