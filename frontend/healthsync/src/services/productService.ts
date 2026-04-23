import { get, post, put, del } from './api';
import { ENDPOINTS } from '../constants';
import type { Product, ProductDetail, CreateProductDTO, ProductCategoryType, Tarja } from '../types';

interface ProductBackendDTO {
id: number;
name: string;
description?: string | null;
priceCost?: number | null;
priceSale?: number | null;
controlled?: boolean;
tarja?: string | null;
registerMS?: string | null;
productCategoryType?: string | null;
category?: string | null;
supplierId?: number | null;
supplierName?: string | null;
barcode?: string | null;
minStock?: number | null;
currentStock?: number | null;
active?: boolean;
lotsCount?: number;
}

interface CreateProductPayload {
name: string;
description?: string | number;
priceCost?: string | number;
priceSale?: string | number;
controlled: boolean;
tarja?: string | number;
registerMS?: string | number;
productCategoryType?: string | number;
supplierId?: string | number;
barcode: string;
minStock?: string | number;
currentStock?: string | number;
active: boolean;
}

// ---------------------------------------------------------------------------
// Helpers
// ---------------------------------------------------------------------------

const sanitizeDigits = (value?: string): string => (value ?? '').replace(/\D/g, '');

const unwrapApiData = <T>(payload: unknown): T => {
if (payload && typeof payload === 'object' && 'data' in payload) {
    return (payload as { data: T }).data;
  }
  return payload as T;
};

const normalizeOptional = (
  value?: string | number | null
): string | number | undefined => {
  if (value === undefined || value === null) return undefined;
  if (typeof value === 'string') {
    const trimmed = value.trim();
    return trimmed ? trimmed : undefined;
  }
  return value;
};

// ---------------------------------------------------------------------------
// Mappers
// ---------------------------------------------------------------------------

const mapProduct = (dto: ProductBackendDTO): Product => ({
  id: dto.id,
  name: dto.name,
  barcode: dto.barcode ?? null,
  description: dto.description ?? null,
  price: typeof dto.priceSale === 'number' ? dto.priceSale : null,
  priceCost: typeof dto.priceCost === 'number' ? dto.priceCost : null,
  priceSale: typeof dto.priceSale === 'number' ? dto.priceSale : null,
  controlled: dto.controlled === true,
  tarja: (dto.tarja as Tarja) ?? null,
  registerMS: dto.registerMS ?? null,
  category: (dto.productCategoryType ?? dto.category) as ProductCategoryType | null,
  supplierId: dto.supplierId ?? null,
  supplierName: dto.supplierName ?? null,
  minStock: typeof dto.minStock === 'number' ? dto.minStock : null,
  currentStock: typeof dto.currentStock === 'number' ? dto.currentStock : null,
  active: dto.active !== false,
});

const mapCreatePayload = (data: CreateProductDTO): CreateProductPayload => ({
  name: data.name.trim(),
  description: normalizeOptional(data.description),
  priceCost: normalizeOptional(data.priceCost),
  priceSale: normalizeOptional(data.priceSale),
  controlled: data.controlled === true,
  tarja: normalizeOptional(data.tarja),
  registerMS: normalizeOptional(data.registerMS),
  productCategoryType: normalizeOptional(data.category),
  supplierId: normalizeOptional(data.supplierId),
  barcode: sanitizeDigits(data.barcode),
  minStock: normalizeOptional(data.minStock),
  currentStock: normalizeOptional(data.currentStock),
  active: data.active === undefined ? true : Boolean(data.active),
});

// ---------------------------------------------------------------------------
// Error wrapper — normalises backend errors into user-friendly messages
// ---------------------------------------------------------------------------

function handleServiceError(context: string, error: unknown): never {
  console.error(`[productService] ${context}:`, error);
  if (error instanceof Error) {
    throw new Error(error.message);
  }
  throw new Error('Ocorreu um erro inesperado. Tente novamente.');
}

// ---------------------------------------------------------------------------
// Service
// ---------------------------------------------------------------------------

export const productService = {
  getAll: async (): Promise<Product[]> => {
    try {
      const response = await get<ProductBackendDTO[]>(ENDPOINTS.PRODUCTS);
      const payload = unwrapApiData<ProductBackendDTO[]>(response.data);
      return Array.isArray(payload) ? payload.map(mapProduct) : [];
    } catch (error) {
      handleServiceError('getAll', error);
    }
  },

  getById: async (id: number): Promise<Product> => {
    try {
      const response = await get<ProductBackendDTO>(ENDPOINTS.PRODUCT_BY_ID(id));
      return mapProduct(unwrapApiData<ProductBackendDTO>(response.data));
    } catch (error) {
      handleServiceError(`getById(${id})`, error);
    }
  },

  getDetail: async (id: number): Promise<ProductDetail> => {
    try {
      const response = await get<ProductBackendDTO>(ENDPOINTS.PRODUCT_DETAIL(id));
      const dto = unwrapApiData<ProductBackendDTO>(response.data);
      return {
        ...mapProduct(dto),
        lotsCount: dto.lotsCount ?? 0,
      };
    } catch (error) {
      handleServiceError(`getDetail(${id})`, error);
    }
  },

  getBySupplier: async (supplierId: number): Promise<Product[]> => {
    try {
      const response = await get<ProductBackendDTO[]>(
        ENDPOINTS.PRODUCTS_BY_SUPPLIER(supplierId)
      );
      const payload = unwrapApiData<ProductBackendDTO[]>(response.data);
      return Array.isArray(payload) ? payload.map(mapProduct) : [];
    } catch (error) {
      handleServiceError(`getBySupplier(${supplierId})`, error);
    }
  },

  getControlled: async (): Promise<Product[]> => {
    try {
      const response = await get<ProductBackendDTO[]>(ENDPOINTS.PRODUCTS_CONTROLLED);
      const payload = unwrapApiData<ProductBackendDTO[]>(response.data);
      return Array.isArray(payload) ? payload.map(mapProduct) : [];
    } catch (error) {
      handleServiceError('getControlled', error);
    }
  },

  create: async (data: CreateProductDTO): Promise<Product> => {
    try {
      const response = await post<ProductBackendDTO>(
        ENDPOINTS.PRODUCT_CREATE,
        mapCreatePayload(data)
      );
      return mapProduct(unwrapApiData<ProductBackendDTO>(response.data));
    } catch (error) {
      handleServiceError('create', error);
    }
  },

  update: async (id: number, data: CreateProductDTO): Promise<Product> => {
    try {
      const response = await put<ProductBackendDTO>(
        ENDPOINTS.PRODUCT_UPDATE(id),
        mapCreatePayload(data)
      );
      return mapProduct(unwrapApiData<ProductBackendDTO>(response.data));
    } catch (error) {
      handleServiceError(`update(${id})`, error);
    }
  },



  delete: async (id: number): Promise<void> => {
    try {
      await del(ENDPOINTS.PRODUCT_DELETE(id));
    } catch (error) {
      handleServiceError(`delete(${id})`, error);
    }


  },


};