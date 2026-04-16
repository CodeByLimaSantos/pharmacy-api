export interface CustomerDTO {
  id: number;
  name: string;
  cpf: string;
  email?: string;
  phone?: string;
  address?: string;
}

export interface CustomerDetailDTO {
  id: number;
  name: string;
  cpf: string;
  email?: string;
  phone?: string;
  address?: string;
  purchaseHistory: SaleSummaryDTO[];
  totalPurchases: number;
}

export interface CreateCustomerDTO {
  name: string;
  cpf: string;
  email?: string;
  phone?: string;
  address?: string;
}

export interface SaleSummaryDTO {
  id: number;
  date: string;
  totalAmount: number;
}