import { get, post, put, del } from './api';
import { ENDPOINTS } from '../constants';
import { PaymentMethod } from '../types';
import type { Financial, FinancialDetail, CreateFinancialDTO, FinancialSummary } from '../types';


export const financialService = {
  getAll: async (): Promise<Financial[]> => {
    const response = await get<any[]>(ENDPOINTS.FINANCIAL);
    const data = Array.isArray(response.data) ? response.data : [];
    return data;
  },

  getById: async (id: number): Promise<Financial> => {
    const response = await get<Financial>(ENDPOINTS.FINANCIAL_BY_ID(id));
    return response.data;
  },

  getDetail: async (id: number): Promise<FinancialDetail> => {
    const response = await get<FinancialDetail>(ENDPOINTS.FINANCIAL_DETAIL(id));
    return response.data;
  },

  getPending: async (): Promise<Financial[]> => {
    const response = await get<any[]>(ENDPOINTS.FINANCIAL_PENDING);
    const data = Array.isArray(response.data) ? response.data : [];
    return data;
  },

  getOverdue: async (): Promise<Financial[]> => {
    const response = await get<any[]>(ENDPOINTS.FINANCIAL_OVERDUE);
    const data = Array.isArray(response.data) ? response.data : [];
    return data;
  },

  getReceivable: async (): Promise<Financial[]> => {
    const response = await get<any[]>(ENDPOINTS.FINANCIAL_RECEIVABLE);
    const data = Array.isArray(response.data) ? response.data : [];
    return data;
  },

  getPayable: async (): Promise<Financial[]> => {
    const response = await get<any[]>(ENDPOINTS.FINANCIAL_PAYABLE);
    const data = Array.isArray(response.data) ? response.data : [];
    return data;
  },

  getSummary: async (): Promise<FinancialSummary> => {
    const response = await get<any>(ENDPOINTS.FINANCIAL_SUMMARY);
    const dto = response.data;
    return {
      totalReceivable: dto.totalReceivable || 0,
      totalPayable: dto.totalPayable || 0,
      receivable: dto.totalReceivable || dto.receivable || 0,
      payable: dto.totalPayable || dto.payable || 0,
      balance: (dto.totalReceivable || 0) - (dto.totalPayable || 0),
      pending: dto.pendingReceivableCount + dto.pendingPayableCount || dto.pending || 0,
      overdue: dto.overdueCount || dto.overdue || 0,
      overdueCount: dto.overdueCount || 0,
    };
  },

  // Backend CreateFinancialDTO: { type, description, amount, dueDate, customerId, supplierId, notes }
  create: async (data: CreateFinancialDTO): Promise<Financial> => {
    const response = await post<Financial>(ENDPOINTS.FINANCIAL_CREATE, {
      type: data.type,
      description: data.description,
      amount: data.amount,
      dueDate: data.dueDate,
      customerId: data.customerId,
      supplierId: data.supplierId,
      notes: data.notes,
    });
    return response.data;
  },

  // Backend UpdateFinancialDTO: { description, amount, dueDate, status, paymentMethod, notes }
  update: async (id: number, data: CreateFinancialDTO): Promise<Financial> => {
    const response = await put<Financial>(ENDPOINTS.FINANCIAL_UPDATE(id), {
      description: data.description,
      amount: data.amount,
      dueDate: data.dueDate,
      status: data.status,
      notes: data.notes,
    });
    return response.data;
  },

  markAsPaid: async (id: number, paymentMethod: PaymentMethod): Promise<Financial> => {
    const response = await post<Financial>(`${ENDPOINTS.FINANCIAL_PAY(id)}?paymentMethod=${paymentMethod}`);
    return response.data;
  },

  delete: async (id: number): Promise<void> => {
    await del(ENDPOINTS.FINANCIAL_DELETE(id));
  },
};
