import { create } from 'zustand';
import { persist } from 'zustand/middleware';
import { STORAGE_KEYS } from '../constants';
import type { Product, Customer, Supplier } from '../types';

// ============================================
// Sidebar State
// ============================================
interface SidebarState {
  isOpen: boolean;
  isCollapsed: boolean;
  toggle: () => void;
  setOpen: (open: boolean) => void;
  setCollapsed: (collapsed: boolean) => void;
}

export const useSidebarStore = create<SidebarState>()(
  persist(
    (set) => ({
      isOpen: true,
      isCollapsed: false,
      toggle: () => set((state) => ({ isOpen: !state.isOpen })),
      setOpen: (open) => set({ isOpen: open }),
      setCollapsed: (collapsed) => set({ isCollapsed: collapsed }),
    }),
    {
      name: 'sidebar-storage',
    }
  )
);

// ============================================
// Theme State
// ============================================
interface ThemeState {
  theme: 'light' | 'dark' | 'system';
  setTheme: (theme: 'light' | 'dark' | 'system') => void;
}

export const useThemeStore = create<ThemeState>()(
  persist(
    (set) => ({
      theme: 'light',
      setTheme: (theme) => set({ theme }),
    }),
    {
      name: 'theme-storage',
    }
  )
);

// ============================================
// Toast/Notification State
// ============================================
interface ToastItem {
  id: string;
  type: 'success' | 'error' | 'warning' | 'info';
  title: string;
  message?: string;
  duration?: number;
}

interface ToastState {
  toasts: ToastItem[];
  addToast: (toast: Omit<ToastItem, 'id'>) => string;
  removeToast: (id: string) => void;
  clearAll: () => void;
  success: (title: string, message?: string) => void;
  error: (title: string, message?: string) => void;
  warning: (title: string, message?: string) => void;
  info: (title: string, message?: string) => void;
}

export const useToastStore = create<ToastState>((set, get) => ({
  toasts: [],
  addToast: (toast) => {
    const id = `${Date.now()}-${Math.random().toString(36).substr(2, 9)}`;
    set((state) => ({
      toasts: [...state.toasts, { ...toast, id }],
    }));
    // Auto-remove after duration
    const duration = toast.duration ?? 5000;
    if (duration > 0) {
      setTimeout(() => get().removeToast(id), duration);
    }
    return id;
  },
  removeToast: (id) =>
    set((state) => ({
      toasts: state.toasts.filter((t) => t.id !== id),
    })),
  clearAll: () => set({ toasts: [] }),
  success: (title, message) => get().addToast({ type: 'success', title, message }),
  error: (title, message) => get().addToast({ type: 'error', title, message }),
  warning: (title, message) => get().addToast({ type: 'warning', title, message }),
  info: (title, message) => get().addToast({ type: 'info', title, message }),
}));

// Legacy alias
export const useNotificationStore = useToastStore;

// ============================================
// User/Auth State
// ============================================
interface UserState {
  user: {
    id: number;
    name: string;
    email: string;
    role: string;
  } | null;
  token: string | null;
  isAuthenticated: boolean;
  setUser: (user: UserState['user']) => void;
  setToken: (token: string | null) => void;
  logout: () => void;
}

export const useUserStore = create<UserState>()(
  persist(
    (set) => ({
      user: null,
      token: null,
      isAuthenticated: false,
      setUser: (user) => set({ user, isAuthenticated: !!user }),
      setToken: (token) => {
        if (token) {
          localStorage.setItem(STORAGE_KEYS.TOKEN, token);
        } else {
          localStorage.removeItem(STORAGE_KEYS.TOKEN);
        }
        set({ token, isAuthenticated: !!token });
      },
      logout: () => {
        localStorage.removeItem(STORAGE_KEYS.TOKEN);
        set({ user: null, token: null, isAuthenticated: false });
      },
    }),
    {
      name: 'healthsync-user-storage',
    }
  )
);

// ============================================
// Modal State
// ============================================
interface ModalState {
  modals: Record<string, boolean>;
  openModal: (id: string) => void;
  closeModal: (id: string) => void;
  toggleModal: (id: string) => void;
  isOpen: (id: string) => boolean;
}

export const useModalStore = create<ModalState>((set, get) => ({
  modals: {},
  openModal: (id) => set((state) => ({ modals: { ...state.modals, [id]: true } })),
  closeModal: (id) => set((state) => ({ modals: { ...state.modals, [id]: false } })),
  toggleModal: (id) =>
    set((state) => ({ modals: { ...state.modals, [id]: !state.modals[id] } })),
  isOpen: (id) => get().modals[id] ?? false,
}));

// ============================================
// App Global State
// ============================================
interface AppState {
  globalLoading: boolean;
  globalError: string | null;
  setGlobalLoading: (loading: boolean) => void;
  setGlobalError: (error: string | null) => void;
}

export const useAppStore = create<AppState>((set) => ({
  globalLoading: false,
  globalError: null,
  setGlobalLoading: (loading) => set({ globalLoading: loading }),
  setGlobalError: (error) => set({ globalError: error }),
}));

// ============================================
// Data Cache Stores
// ============================================
interface CacheState<T> {
  data: T[];
  lastFetch: number | null;
  loading: boolean;
  setData: (data: T[]) => void;
  setLoading: (loading: boolean) => void;
  invalidate: () => void;
  isStale: (maxAge?: number) => boolean;
}

const createCacheStore = <T>() =>
  create<CacheState<T>>((set, get) => ({
    data: [],
    lastFetch: null,
    loading: false,
    setData: (data) => set({ data, lastFetch: Date.now(), loading: false }),
    setLoading: (loading) => set({ loading }),
    invalidate: () => set({ lastFetch: null }),
    isStale: (maxAge = 5 * 60 * 1000) => {
      const { lastFetch } = get();
      if (!lastFetch) return true;
      return Date.now() - lastFetch > maxAge;
    },
  }));

export const useProductsCache = createCacheStore<Product>();
export const useCustomersCache = createCacheStore<Customer>();
export const useSuppliersCache = createCacheStore<Supplier>();

// ============================================
// Dashboard State
// ============================================
interface DashboardData {
  totalSales: number;
  totalRevenue: number;
  totalCustomers: number;
  totalProducts: number;
  lowStockProducts: number;
  recentSales: unknown[];
  topProducts: unknown[];
}

interface DashboardState {
  data: DashboardData | null;
  loading: boolean;
  lastFetch: number | null;
  setData: (data: DashboardData) => void;
  setLoading: (loading: boolean) => void;
  invalidate: () => void;
  isStale: (maxAge?: number) => boolean;
}

export const useDashboardStore = create<DashboardState>((set, get) => ({
  data: null,
  loading: false,
  lastFetch: null,
  setData: (data) => set({ data, lastFetch: Date.now(), loading: false }),
  setLoading: (loading) => set({ loading }),
  invalidate: () => set({ lastFetch: null }),
  isStale: (maxAge = 2 * 60 * 1000) => {
    const { lastFetch } = get();
    if (!lastFetch) return true;
    return Date.now() - lastFetch > maxAge;
  },
}));
