export interface ProductDTO {
  id: number;
  name: string;
  description: string;
  priceCost: number;
  priceSale: number;
  controlled: boolean;
  tarja?: string;
  registerMS?: string;
  productCategoryType?: string;
  supplierId: number;
  supplierName?: string;
}

export interface ProductDetailDTO {
  id: number;
  name: string;
  description: string;
  priceCost: number;
  priceSale: number;
  controlled: boolean;
  tarja?: string;
  registerMS?: string;
  productCategoryType?: string;
  supplierId: number;
  supplierName?: string;
  currentStock: number;
}

export interface CreateProductDTO {
  name: string;
  description: string;
  priceCost: number;
  priceSale: number;
  controlled: boolean;
  tarja?: string;
  registerMS?: string;
  productCategoryType?: string;
  supplierId: number;
}

export interface ProductStockAlert {
  productId: number;
  productName: string;
  currentStock: number;
  minimumStock: number;
  status: 'LOW_STOCK' | 'OUT_OF_STOCK' | 'OK';
}