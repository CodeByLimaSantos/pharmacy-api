// API Configuration
export const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080';

// API Endpoints — mapped to Spring Boot controllers (Swagger source of truth)
export const ENDPOINTS = {
  // Auth (AuthController)
  AUTH_LOGIN: '/auth/login',
  AUTH_REGISTER: '/auth/register',

  // Products (ProductController: @RequestMapping("/product"))
  PRODUCTS: '/product/search',
  PRODUCT_BY_ID: (id: number) => `/product/search/${id}`,
  PRODUCT_DETAIL: (id: number) => `/product/searchDetails/${id}`,
  PRODUCT_CREATE: '/product/create',
  PRODUCT_UPDATE: (id: number) => `/product/${id}`,
  PRODUCT_DELETE: (id: number) => `/product/delete/${id}`,
  PRODUCTS_BY_SUPPLIER: (supplierId: number) => `/product/supplier/${supplierId}`,
  PRODUCTS_CONTROLLED: '/product/controlled',

  // Customers (CustomerController: @RequestMapping("/customer"))
  CUSTOMERS: '/customer/search',
  CUSTOMER_BY_ID: (id: number) => `/customer/search/${id}`,
  CUSTOMER_DETAIL: (id: number) => `/customer/searchDetails/${id}`,
  CUSTOMER_CREATE: '/customer/create',
  CUSTOMER_UPDATE: (id: number) => `/customer/update/${id}`,
  CUSTOMER_DELETE: (id: number) => `/customer/delete/${id}`,
  CUSTOMER_BY_CPF: (cpf: string) => `/customer/cpf/${cpf}`,

  // Suppliers (SupplierController: @RequestMapping("/suppliers"))
  SUPPLIERS: '/suppliers/all',
  SUPPLIER_BY_ID: (id: number) => `/suppliers/search/${id}`,
  SUPPLIER_DETAIL: (id: number) => `/suppliers/searchDetails/${id}`,
  SUPPLIER_CREATE: '/suppliers/create',
  SUPPLIER_UPDATE: (id: number) => `/suppliers/update/${id}`,
  SUPPLIER_DELETE: (id: number) => `/suppliers/remove/${id}`,
  SUPPLIER_BY_CNPJ: (cnpj: string) => `/suppliers/cnpj/${cnpj}`,

  // Sales (SaleController: @RequestMapping("/sales"))
  SALES: '/sales/all',
  SALE_BY_ID: (id: number) => `/sales/${id}`,
  SALE_DETAIL: (id: number) => `/sales/searchDetails/${id}`,
  SALE_CREATE: '/sales/create',
  SALE_DELETE: (id: number) => `/sales/${id}`,
  SALES_BY_CUSTOMER: (customerId: number) => `/sales/customer/${customerId}`,
  SALES_TOTAL: '/sales/total/amount',
  SALES_CUSTOMER_TOTAL: (customerId: number) => `/sales/customer/${customerId}/total`,

  // Financial (FinancialController: @RequestMapping("/financial"))
  FINANCIAL: '/financial',
  FINANCIAL_BY_ID: (id: number) => `/financial/search/${id}`,
  FINANCIAL_DETAIL: (id: number) => `/financial/detail/${id}`,
  FINANCIAL_CREATE: '/financial/create',
  FINANCIAL_UPDATE: (id: number) => `/financial/update/${id}`,
  FINANCIAL_DELETE: (id: number) => `/financial/delete/${id}`,
  FINANCIAL_PENDING: '/financial/pending',
  FINANCIAL_OVERDUE: '/financial/overdue',
  FINANCIAL_RECEIVABLE: '/financial/receivable',
  FINANCIAL_PAYABLE: '/financial/payable',
  FINANCIAL_PAY: (id: number) => `/financial/${id}/pay`,
  FINANCIAL_SUMMARY: '/financial/summary',

  // Inventory (InventoryController: @RequestMapping("/inventory"))
  INVENTORY_CREATE: '/inventory/create',
  INVENTORY_LOTS: '/inventory/lots',
  INVENTORY_ALL: '/inventory/all',
  INVENTORY_LOT_BY_ID: (id: number) => `/inventory/lots/${id}`,
  INVENTORY_PRODUCT_STOCK: (productId: number) => `/inventory/product/${productId}/stock`,
  INVENTORY_BEST_LOT: '/inventory/lots/best-sale',
  INVENTORY_EXPIRED: '/inventory/lots/expired',
  INVENTORY_EXPIRING: (days: number) => `/inventory/lots/expiring/${days}`,
  INVENTORY_MOVEMENTS_AUDIT: '/inventory/movements/audit',
  INVENTORY_LOT_ENTRY: (lotId: number) => `/inventory/lots/${lotId}/entry`,
  INVENTORY_LOT_SALE_EXIT: (lotId: number) => `/inventory/lots/${lotId}/sale-exit`,
  INVENTORY_LOT_ADJUSTMENT_IN: (lotId: number) => `/inventory/lots/${lotId}/adjustment-in`,
  INVENTORY_LOT_ADJUSTMENT_OUT: (lotId: number) => `/inventory/lots/${lotId}/adjustment-out`,
  INVENTORY_LOT_DISPOSAL: (lotId: number) => `/inventory/lots/${lotId}/disposal`,
  INVENTORY_LOT_HISTORY: (lotId: number) => `/inventory/lots/${lotId}/history`,
  INVENTORY_PROCESS_EXPIRED: '/inventory/process-expired',
} as const;

// Pagination
export const DEFAULT_PAGE_SIZE = 10;
export const PAGE_SIZE_OPTIONS = [10, 20, 50, 100];

// Date formats
export const DATE_FORMAT = 'dd/MM/yyyy';
export const DATE_TIME_FORMAT = 'dd/MM/yyyy HH:mm';

// Timeouts
export const API_TIMEOUT = 30000; // 30 seconds

// Local Storage Keys
export const STORAGE_KEYS = {
  THEME: 'healthsync-theme',
  USER: 'healthsync-user',
  TOKEN: 'healthsync-token',
} as const;
