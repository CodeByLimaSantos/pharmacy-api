export interface SupplierDTO {
  id: number;
  name: string;
  cnpj: string;
  email?: string;
  phone?: string;
  address?: string;
}

export interface SupplierDetailDTO {
  id: number;
  name: string;
  cnpj: string;
  email?: string;
  phone?: string;
  address?: string;
  products: ProductSummaryDTO[];
}

export interface CreateSupplierDTO {
  name: string;
  cnpj: string;
  email?: string;
  phone?: string;
  address?: string;
}

export interface ProductSummaryDTO {
  id: number;
  name: string;
  priceSale: number;
}