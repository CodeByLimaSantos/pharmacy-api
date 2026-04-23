import { get, post, put, del } from './api';
import { ENDPOINTS } from '../constants';
import type { Supplier, SupplierDetail, CreateSupplierDTO } from '../types';

// Map backend SupplierDTO → frontend Supplier interface
const mapSupplier = (dto: any): Supplier => ({
  id: dto.id,
  name: dto.name,
  cnpj: dto.cnpj || '',
  email: dto.email || null,
  phone: dto.phone || null,
  address: dto.address || null,
  contactName: dto.contactName || null,
  active: dto.active !== false,
});

export const supplierService = {
  getAll: async (): Promise<Supplier[]> => {
    const response = await get<any[]>(ENDPOINTS.SUPPLIERS);
    const data = Array.isArray(response.data) ? response.data : [];
    return data.map(mapSupplier);
  },

  getById: async (id: number): Promise<Supplier> => {
    const response = await get<any>(ENDPOINTS.SUPPLIER_BY_ID(id));
    return mapSupplier(response.data);
  },

  getDetail: async (id: number): Promise<SupplierDetail> => {
    const response = await get<any>(ENDPOINTS.SUPPLIER_DETAIL(id));
    const dto = response.data;
    return {
      ...mapSupplier(dto),
      productsCount: dto.productsCount || 0,
      totalProductsValue: dto.totalProductsValue || 0,
    };
  },

  getByCnpj: async (cnpj: string): Promise<Supplier> => {
    const response = await get<any>(ENDPOINTS.SUPPLIER_BY_CNPJ(cnpj));
    return mapSupplier(response.data);
  },

  // Backend CreateSupplierDTO: { name, cnpj, email, phone }
  create: async (data: CreateSupplierDTO): Promise<Supplier> => {
    const response = await post<any>(ENDPOINTS.SUPPLIER_CREATE, {
      name: data.name,
      cnpj: data.cnpj,
      email: data.email,
      phone: data.phone,
    });
    return mapSupplier(response.data);
  },

  // Backend UpdateSupplierDTO: { name, email, phone }
  update: async (id: number, data: CreateSupplierDTO): Promise<Supplier> => {
    const response = await put<any>(ENDPOINTS.SUPPLIER_UPDATE(id), {
      name: data.name,
      email: data.email,
      phone: data.phone,
    });
    return mapSupplier(response.data);
  },

  delete: async (id: number): Promise<void> => {
    await del(ENDPOINTS.SUPPLIER_DELETE(id));
  },
};
