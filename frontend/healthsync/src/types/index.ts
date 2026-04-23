// ==========================================
// ENUMS (as const objects for TypeScript compatibility)
// ==========================================

export const ProductCategoryType = {
  MEDICAMENTOS: 'MEDICAMENTOS',
  GENERICOS: 'GENERICOS',
  SIMILARES: 'SIMILARES',
  PERFUMARIA: 'PERFUMARIA',
  HIGIENE_PESSOAL: 'HIGIENE_PESSOAL',
  INFANTIL: 'INFANTIL',
  DERMOCOSMETICOS: 'DERMOCOSMETICOS',
  SAUDE_SEXUAL: 'SAUDE_SEXUAL',
  SUPLEMENTOS: 'SUPLEMENTOS',
} as const;

export type ProductCategoryType = (typeof ProductCategoryType)[keyof typeof ProductCategoryType];

export const ProductCategoryTypeLabels: Record<ProductCategoryType, string> = {
  [ProductCategoryType.MEDICAMENTOS]: 'Medicamentos',
  [ProductCategoryType.GENERICOS]: 'Genéricos',
  [ProductCategoryType.SIMILARES]: 'Similares',
  [ProductCategoryType.PERFUMARIA]: 'Perfumaria',
  [ProductCategoryType.HIGIENE_PESSOAL]: 'Higiene Pessoal',
  [ProductCategoryType.INFANTIL]: 'Linha Infantil',
  [ProductCategoryType.DERMOCOSMETICOS]: 'Dermocosméticos',
  [ProductCategoryType.SAUDE_SEXUAL]: 'Saúde Sexual',
  [ProductCategoryType.SUPLEMENTOS]: 'Suplementos e Vitaminas',
};

export const PaymentMethod = {
  CREDIT_CARD: 'CREDIT_CARD',
  DEBIT_CARD: 'DEBIT_CARD',
  CASH: 'CASH',
  BANK_TRANSFER: 'BANK_TRANSFER',
  PIX: 'PIX',
  BOLETO: 'BOLETO',
} as const;

export type PaymentMethod = (typeof PaymentMethod)[keyof typeof PaymentMethod];

export const PaymentMethodLabels: Record<PaymentMethod, string> = {
  [PaymentMethod.CREDIT_CARD]: 'Cartão de Crédito',
  [PaymentMethod.DEBIT_CARD]: 'Cartão de Débito',
  [PaymentMethod.CASH]: 'Dinheiro',
  [PaymentMethod.BANK_TRANSFER]: 'Transferência Bancária',
  [PaymentMethod.PIX]: 'PIX',
  [PaymentMethod.BOLETO]: 'Boleto',
};

export const FinancialType = {
  RECEIVABLE: 'CONTA_A_RECEBER',
  PAYABLE: 'CONTA_A_PAGAR',
} as const;

export type FinancialType = (typeof FinancialType)[keyof typeof FinancialType];

export const FinancialTypeLabels: Record<FinancialType, string> = {
  [FinancialType.RECEIVABLE]: 'A Receber',
  [FinancialType.PAYABLE]: 'A Pagar',
};

export const PaymentStatus = {
  PENDING: 'PENDING',
  PARTIALLY_PAID: 'PARTIALLY_PAID',
  PAID: 'PAID',
  OVERDUE: 'OVERDUE',
  CANCELLED: 'CANCELED',
} as const;

export type PaymentStatus = (typeof PaymentStatus)[keyof typeof PaymentStatus];

export const PaymentStatusLabels: Record<PaymentStatus, string> = {
  [PaymentStatus.PENDING]: 'Pendente',
  [PaymentStatus.PARTIALLY_PAID]: 'Parcialmente Pago',
  [PaymentStatus.PAID]: 'Pago',
  [PaymentStatus.OVERDUE]: 'Vencido',
  [PaymentStatus.CANCELLED]: 'Cancelado',
};

export const MovementType = {
  ENTRY: 'ENTRY',
  SALE_EXIT: 'SALE_EXIT',
  ADJUSTMENT_IN: 'ADJUSTMENT_IN',
  ADJUSTMENT_OUT: 'ADJUSTMENT_OUT',
  DISPOSAL: 'DISPOSAL',
} as const;

export type MovementType = (typeof MovementType)[keyof typeof MovementType];

export const MovementTypeLabels: Record<MovementType, string> = {
  [MovementType.ENTRY]: 'Entrada',
  [MovementType.SALE_EXIT]: 'Saída por Venda',
  [MovementType.ADJUSTMENT_IN]: 'Ajuste de Entrada',
  [MovementType.ADJUSTMENT_OUT]: 'Ajuste de Saída',
  [MovementType.DISPOSAL]: 'Descarte',
};

// Tarja options
export const TarjaOptions = [
  'Sem Tarja',
  'Tarja Vermelha',
  'Tarja Preta',
  'Tarja Amarela',
] as const;

export type Tarja = (typeof TarjaOptions)[number];

// ==========================================
// ENTITIES / DTOs
// ==========================================

// Product
export interface Product {
  id: number;
  name: string;
  barcode?: string | null;
  description?: string | null;
  price?: number | null;
  priceCost?: number | null;
  priceSale?: number | null;
  controlled?: boolean;
  tarja?: Tarja | null;
  registerMS?: string | null;
  category?: ProductCategoryType | null;
  supplierId?: number | null;
  supplierName?: string | null;
  minStock?: number | null;
  currentStock?: number | null;
  active?: boolean;
}

export interface ProductDetail extends Product {
  lotsCount: number;
}

export interface CreateProductDTO {
  name: string;
  barcode?: string;
  description?: string;
  price?: number;
  priceCost?: number;
  priceSale: number;
  controlled?: boolean;
  tarja?: string;
  registerMS?: string;
  category?: ProductCategoryType;
  supplierId?: number;
  minStock?: number;
  currentStock?: number;
  active?: boolean;
}

// Customer
export interface Customer {
  id: number;
  name: string;
  cpf: string;
  email?: string | null;
  phone?: string | null;
  address?: string | null;
  active: boolean;
}

export interface CustomerDetail extends Customer {
  totalPurchases: number;
  purchaseCount: number;
}

export interface CreateCustomerDTO {
  name: string;
  cpf: string;
  email?: string;
  phone?: string;
  address?: string;
  active?: boolean;
}

// Supplier
export interface Supplier {
  id: number;
  name: string;
  cnpj: string;
  email: string | null;
  phone: string | null;
  address?: string | null;
  contactName?: string | null;
  active: boolean;
}

export interface SupplierDetail extends Supplier {
  productsCount: number;
  totalProductsValue: number;
}

export interface CreateSupplierDTO {
  name: string;
  cnpj?: string;
  email?: string;
  phone?: string;
  address?: string;
  contactName?: string;
  active?: boolean;
}

// Sale
export interface Sale {
  id: number;
  customerId: number | null;
  customerName: string | null;
  saleDate: string;
  totalAmount: number;
  paymentMethod: PaymentMethod;
  itemsCount: number;
}

export interface SaleItem {
  id: number;
  productId: number;
  productName: string;
  quantity: number;
  priceAtSale: number;
  subtotal: number;
}

export interface SaleDetail extends Sale {
  items: SaleItem[];
}

export interface CreateSaleItemDTO {
  productId: number;
  quantity: number;
  unitPrice?: number;
  discount?: number;
}

export interface CreateSaleDTO {
  customerId?: number;
  paymentMethod: PaymentMethod;
  items: CreateSaleItemDTO[];
}

// Financial
export interface Financial {
  id: number;
  type: FinancialType;
  description: string;
  amount: number;
  issueDate: string;
  dueDate: string;
  paymentDate: string | null;
  status: PaymentStatus;
  paymentMethod: PaymentMethod | null;
  notes?: string | null;
}

export interface FinancialDetail extends Financial {
  customerId: number | null;
  customerName: string | null;
  supplierId: number | null;
  supplierName: string | null;
}

export interface CreateFinancialDTO {
  type: FinancialType;
  description: string;
  amount: number;
  dueDate: string;
  paymentDate?: string;
  status?: PaymentStatus;
  customerId?: number;
  supplierId?: number;
  notes?: string;
}

export interface FinancialSummary {
  totalReceivable: number;
  totalPayable: number;
  receivable: number;
  payable: number;
  balance: number;
  pending: number;
  overdue: number;
  overdueCount: number;
}

// Inventory
export interface InventoryLot {
  id: number;
  productId: number;
  productName: string;
  lotNumber: string;
  entryDate: string;
  expirationDate: string;
  quantity: number;
  availableQuantity: number;
}

export interface InventoryMovement {
  id: number;
  inventoryLotId: number;
  lotNumber: string;
  movementType: MovementType;
  quantity: number;
  movementDate: string;
  reason: string | null;
}

export interface CreateInventoryLotDTO {
  productId: number;
  lotNumber: string;
  expirationDate: string;
  quantity: number;
}

export interface CreateInventoryMovementDTO {
  productId: number;
  type: MovementType;
  quantity: number;
  reason?: string;
  lotNumber?: string;
  expirationDate?: string;
}

// ==========================================
// API Response Types
// ==========================================

export interface ApiError {
  timestamp?: string;
  status?: number;
  error?: string;
  message?: string;
  details?: string;
  path?: string;
  httpStatus?: number;
  fieldErrors?: Record<string, string>;
}

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  code: string;
  timestamp: string;
  path: string;
  data: T;
}

// ==========================================
// UI Types
// ==========================================

export type LoadingState = 'idle' | 'loading' | 'success' | 'error';

export interface PaginatedResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}
