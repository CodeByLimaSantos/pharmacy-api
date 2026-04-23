import { get, post, put, del } from './api';
import { ENDPOINTS } from '../constants';
import type { Customer, CustomerDetail, CreateCustomerDTO } from '../types';

const sanitizeDigits = (value?: string): string => (value ?? '').replace(/\D/g, '');

const unwrapApiData = <T>(payload: unknown): T => {
  if (payload && typeof payload === 'object' && 'data' in payload) {
    return (payload as { data: T }).data;
  }
  return payload as T;
};

const normalizeOptional = (value?: string): string | undefined => {
  const trimmed = value?.trim();
  return trimmed ? trimmed : undefined;
};

// Map backend CustomerDTO → frontend Customer interface
const mapCustomer = (dto: any): Customer => ({
  id: dto.id,
  name: dto.name,
  cpf: dto.cpf || '',
  email: dto.email || null,
  phone: dto.phone || null,
  address: dto.address || null,
  active: dto.active !== false,
});

export const customerService = {
  getAll: async (): Promise<Customer[]> => {
    const response = await get<any[]>(ENDPOINTS.CUSTOMERS);
    const payload = unwrapApiData<any[]>(response.data);
    const data = Array.isArray(payload) ? payload : [];
    return data.map(mapCustomer);
  },

  getById: async (id: number): Promise<Customer> => {
    const response = await get<any>(ENDPOINTS.CUSTOMER_BY_ID(id));
    return mapCustomer(unwrapApiData<any>(response.data));
  },

  getDetail: async (id: number): Promise<CustomerDetail> => {
    const response = await get<any>(ENDPOINTS.CUSTOMER_DETAIL(id));
    const dto = unwrapApiData<any>(response.data);
    return {
      ...mapCustomer(dto),
      totalPurchases: dto.totalPurchases || 0,
      purchaseCount: dto.totalPurchases || dto.purchaseCount || 0,
    };
  },

  getByCpf: async (cpf: string): Promise<Customer> => {
    const response = await get<any>(ENDPOINTS.CUSTOMER_BY_CPF(sanitizeDigits(cpf)));
    return mapCustomer(unwrapApiData<any>(response.data));
  },

  // Backend CreateCustomerDTO expects: { name, cpf, email?, phone?, address? }
  create: async (data: CreateCustomerDTO): Promise<Customer> => {
    const response = await post<any>(ENDPOINTS.CUSTOMER_CREATE, {
      name: data.name.trim(),
      cpf: sanitizeDigits(data.cpf),
      email: normalizeOptional(data.email),
      phone: sanitizeDigits(data.phone),
      address: normalizeOptional(data.address),
    });

    return mapCustomer(unwrapApiData<any>(response.data));
  },
  // Backend update endpoint receives the same contract used by create
  update: async (id: number, data: CreateCustomerDTO): Promise<Customer> => {
    const response = await put<any>(ENDPOINTS.CUSTOMER_UPDATE(id), {
      name: data.name.trim(),
      cpf: sanitizeDigits(data.cpf),
      email: normalizeOptional(data.email),
      phone: sanitizeDigits(data.phone),
      address: normalizeOptional(data.address),
    });

    return mapCustomer(unwrapApiData<any>(response.data));
  },

  delete: async (id: number): Promise<void> => {
    await del(ENDPOINTS.CUSTOMER_DELETE(id));
  },
};
