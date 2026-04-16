export interface FinancialDTO {
  id: number;
  description: string;
  amount: number;
  dueDate: string;
  paymentDate?: string;
  status: PaymentStatus;
  type: FinancialType;
  customerId?: number;
  customerName?: string;
  supplierId?: number;
  supplierName?: string;
  paymentMethod?: PaymentMethod;
  notes?: string;
}

export interface FinancialDetailDTO {
  id: number;
  description: string;
  amount: number;
  dueDate: string;
  paymentDate?: string;
  status: PaymentStatus;
  type: FinancialType;
  customerId?: number;
  customerName?: string;
  supplierId?: number;
  supplierName?: string;
  paymentMethod?: PaymentMethod;
  notes?: string;
  createdAt: string;
  updatedAt: string;
}

export interface CreateFinancialDTO {
  description: string;
  amount: number;
  dueDate: string;
  type: FinancialType;
  customerId?: number;
  supplierId?: number;
  notes?: string;
}

export interface FinancialSummaryDTO {
  totalReceivable: number;
  totalPayable: number;
  totalReceived: number;
  totalPaid: number;
  pendingReceivable: number;
  pendingPayable: number;
  overdueReceivable: number;
  overduePayable: number;
  countPendingReceivable: number;
  countPendingPayable: number;
  countOverdueReceivable: number;
  countOverduePayable: number;
  receivedRate: number;
  paidRate: number;
}

export enum FinancialType {
  CONTA_A_RECEBER = 'CONTA_A_RECEBER',
  CONTA_A_PAGAR = 'CONTA_A_PAGAR'
}

export enum PaymentStatus {
  PENDING = 'PENDING',
  PAID = 'PAID',
  PARTIALLY_PAID = 'PARTIALLY_PAID',
  OVERDUE = 'OVERDUE'
}

export enum PaymentMethod {
  CASH = 'CASH',
  CREDIT_CARD = 'CREDIT_CARD',
  DEBIT_CARD = 'DEBIT_CARD',
  PIX = 'PIX',
  BANK_TRANSFER = 'BANK_TRANSFER'
}