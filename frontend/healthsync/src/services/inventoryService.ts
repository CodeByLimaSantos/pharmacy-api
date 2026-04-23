import { get, post } from './api';
import { ENDPOINTS } from '../constants';
import type { InventoryLot, InventoryMovement, CreateInventoryLotDTO } from '../types';

export const inventoryService = {
  getAllLots: async (): Promise<InventoryLot[]> => {
    const response = await get<any[]>(ENDPOINTS.INVENTORY_ALL);
    const data = Array.isArray(response.data) ? response.data : [];
    return data;
  },

  getLotById: async (id: number): Promise<InventoryLot> => {
    const response = await get<InventoryLot>(ENDPOINTS.INVENTORY_LOT_BY_ID(id));
    return response.data;
  },

  getProductStock: async (productId: number): Promise<number> => {
    const response = await get<any>(ENDPOINTS.INVENTORY_PRODUCT_STOCK(productId));
    // Response may be a number or an object with totalStock
    const data = response.data;
    return typeof data === 'number' ? data : data?.totalStock || 0;
  },

  getBestLotForSale: async (productId: number, quantity: number): Promise<InventoryLot | null> => {
    const response = await get<InventoryLot>(ENDPOINTS.INVENTORY_BEST_LOT, { productId, quantity });
    return response.data;
  },

  getExpiredLots: async (): Promise<InventoryLot[]> => {
    const response = await get<any[]>(ENDPOINTS.INVENTORY_EXPIRED);
    const data = Array.isArray(response.data) ? response.data : [];
    return data;
  },

  getExpiringLots: async (days: number): Promise<InventoryLot[]> => {
    const response = await get<any[]>(ENDPOINTS.INVENTORY_EXPIRING(days));
    const data = Array.isArray(response.data) ? response.data : [];
    return data;
  },

  getMovementsAudit: async (): Promise<InventoryMovement[]> => {
    const response = await get<any[]>(ENDPOINTS.INVENTORY_MOVEMENTS_AUDIT);
    const data = Array.isArray(response.data) ? response.data : [];
    return data;
  },

  getLotHistory: async (lotId: number): Promise<InventoryMovement[]> => {
    const response = await get<any[]>(ENDPOINTS.INVENTORY_LOT_HISTORY(lotId));
    const data = Array.isArray(response.data) ? response.data : [];
    return data;
  },

  // Backend POST /inventory/create
  createLot: async (data: CreateInventoryLotDTO): Promise<InventoryLot> => {
    const response = await post<InventoryLot>(ENDPOINTS.INVENTORY_CREATE, data);
    return response.data;
  },

  registerEntry: async (lotId: number, quantity: number): Promise<InventoryMovement> => {
    const response = await post<InventoryMovement>(`${ENDPOINTS.INVENTORY_LOT_ENTRY(lotId)}?quantity=${quantity}`);
    return response.data;
  },

  registerSaleExit: async (lotId: number, quantity: number): Promise<InventoryMovement> => {
    const response = await post<InventoryMovement>(`${ENDPOINTS.INVENTORY_LOT_SALE_EXIT(lotId)}?quantity=${quantity}`);
    return response.data;
  },

  registerAdjustmentIn: async (lotId: number, quantity: number, reason: string): Promise<InventoryMovement> => {
    const response = await post<InventoryMovement>(
      `${ENDPOINTS.INVENTORY_LOT_ADJUSTMENT_IN(lotId)}?quantity=${quantity}&reason=${encodeURIComponent(reason)}`
    );
    return response.data;
  },

  registerAdjustmentOut: async (lotId: number, quantity: number, reason: string): Promise<InventoryMovement> => {
    const response = await post<InventoryMovement>(
      `${ENDPOINTS.INVENTORY_LOT_ADJUSTMENT_OUT(lotId)}?quantity=${quantity}&reason=${encodeURIComponent(reason)}`
    );
    return response.data;
  },

  registerDisposal: async (lotId: number, quantity: number, reason: string): Promise<InventoryMovement> => {
    const response = await post<InventoryMovement>(
      `${ENDPOINTS.INVENTORY_LOT_DISPOSAL(lotId)}?quantity=${quantity}&reason=${encodeURIComponent(reason)}`
    );
    return response.data;
  },

  processExpiredLots: async (): Promise<InventoryMovement[]> => {
    const response = await post<any[]>(ENDPOINTS.INVENTORY_PROCESS_EXPIRED);
    const data = Array.isArray(response.data) ? response.data : [];
    return data;
  },
};
