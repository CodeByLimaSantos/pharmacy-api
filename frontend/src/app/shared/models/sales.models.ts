export interface SaleDTO {
  id: number;
  date: string;
  customerId?: number;
  customerName?: string;
  totalAmount: number;
  itemsCount: number;
}

export interface SaleDetailDTO {
  id: number;
  date: string;
  customerId?: number;
  customerName?: string;
  totalAmount: number;
  items: SaleItemDTO[];
}

export interface SaleItemDTO {
  id: number;
  productId: number;
  productName: string;
  quantity: number;
  priceAtSale: number;
  subtotal: number;
}

export interface CreateSaleDTO {
  customerId?: number;
  items: CreateSaleItemDTO[];
}

export interface CreateSaleItemDTO {
  productId: number;
  quantity: number;
  priceAtSale: number;
}

export interface SaleSummary {
  totalSales: number;
  totalAmount: number;
  averageTicket: number;
  period: string;
}