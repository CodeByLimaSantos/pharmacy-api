import { get, post, del } from './api';
import { ENDPOINTS } from '../constants';
import type { Sale, SaleDetail, CreateSaleDTO } from '../types';

// Map backend SaleDTO → frontend Sale interface
const mapSale = (dto: any): Sale => ({
  id: dto.id,
  customerId: dto.customerId,
  customerName: dto.customerName || null,
  saleDate: dto.saleDate,
  totalAmount: dto.totalAmount,
  paymentMethod: dto.paymentMethod,
  itemsCount: dto.items?.length || dto.itemsCount || 0,
});

export const saleService = {
  getAll: async (): Promise<Sale[]> => {
    const response = await get<any[]>(ENDPOINTS.SALES);
    const data = Array.isArray(response.data) ? response.data : [];
    return data.map(mapSale);
  },

  getById: async (id: number): Promise<Sale> => {
    const response = await get<any>(ENDPOINTS.SALE_BY_ID(id));
    return mapSale(response.data);
  },

  getDetail: async (id: number): Promise<SaleDetail> => {
    const response = await get<any>(ENDPOINTS.SALE_DETAIL(id));
    const dto = response.data;
    return {
      ...mapSale(dto),
      items: (dto.items || []).map((item: any) => ({
        id: item.id,
        productId: item.productId,
        productName: item.productName || '',
        quantity: item.quantity,
        priceAtSale: item.priceAtSale,
        subtotal: item.subtotal || item.priceAtSale * item.quantity,
      })),
    };
  },

  getByCustomer: async (customerId: number): Promise<Sale[]> => {
    const response = await get<any[]>(ENDPOINTS.SALES_BY_CUSTOMER(customerId));
    const data = Array.isArray(response.data) ? response.data : [];
    return data.map(mapSale);
  },

  getTotalAmount: async (): Promise<number> => {
    const response = await get<number>(ENDPOINTS.SALES_TOTAL);
    return response.data;
  },

  getCustomerTotal: async (customerId: number): Promise<number> => {
    const response = await get<number>(ENDPOINTS.SALES_CUSTOMER_TOTAL(customerId));
    return response.data;
  },

  // Backend CreateSaleDTO: { customerId?, paymentMethod, items[{productId, quantity, priceAtSale}] }
  create: async (data: CreateSaleDTO): Promise<Sale> => {
    const payload = {
      customerId: data.customerId || null,
      paymentMethod: data.paymentMethod,
      items: data.items.map(item => ({
        productId: item.productId,
        quantity: item.quantity,
        priceAtSale: item.unitPrice || 0,
      })),
    };
    const response = await post<any>(ENDPOINTS.SALE_CREATE, payload);
    return mapSale(response.data);
  },

  cancel: async (id: number): Promise<void> => {
    await del(ENDPOINTS.SALE_DELETE(id));
  },
};
