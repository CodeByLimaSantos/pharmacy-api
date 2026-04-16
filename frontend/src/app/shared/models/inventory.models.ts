/**
 * Modelos para módulo de Inventário
 * Correspondem aos DTOs do backend Spring Boot
 */

export interface InventoryLotDTO {
  id: number;
  productId: number;
  productName: string;
  lotNumber: string;
  entryDate: string; // LocalDate as ISO string
  expirationDate: string; // LocalDate as ISO string
  quantity: number;
  availableQuantity?: number;
}

export interface InventoryMovementDTO {
  id: number;
  lotId?: number;
  lotNumber?: string;
  productName?: string;
  movementType: MovementType;
  quantity: number;
  reason?: string;
  timestamp?: string;
}

export interface CreateInventoryLotDTO {
  productId: number;
  lotNumber: string;
  expirationDate: string;
  quantity: number;
}

export interface InventoryLotDetailDTO {
  id: number;
  productId: number;
  productName: string;
  lotNumber: string;
  entryDate: string;
  expirationDate: string;
  initialQuantity: number;
  currentQuantity: number;
  totalMoved: number;
  isExpired: boolean;
  daysUntilExpiration?: number;
  movements: InventoryMovementDTO[];
}

export interface StockInfo {
  productId: number;
  productName: string;
  totalStock: number;
  lotsCount: number;
  expiredLotsCount: number;
  expiringSoonCount: number;
}

export interface LotAlert {
  lotId: number;
  lotNumber: string;
  productName: string;
  expirationDate: string;
  daysUntilExpiration: number;
  alertType: 'EXPIRED' | 'EXPIRING_SOON';
  availableQuantity: number;
}

/**
 * Tipos de movimentação de inventário
 * Deve corresponder ao enum MovementType do backend
 */
export enum MovementType {
  ENTRY = 'ENTRY',
  SALE_EXIT = 'SALE_EXIT',
  ADJUSTMENT_IN = 'ADJUSTMENT_IN',
  ADJUSTMENT_OUT = 'ADJUSTMENT_OUT',
  DISPOSAL = 'DISPOSAL'
}

/**
 * Filtros para listagem de movimentações
 */
export interface MovementFilter {
  lotId?: number;
  movementType?: MovementType;
  startDate?: string;
  endDate?: string;
  hasReason?: boolean;
}