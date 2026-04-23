import axios from 'axios';
import { API_BASE_URL, ENDPOINTS, STORAGE_KEYS } from '../constants';
import { useUserStore } from '../store';

interface LoginRequest {
  username: string;
  password: string;
}

interface LoginResponse {
  token: string;
}

interface RegisterRequest {
  username: string;
  password: string;
  email: string;
  role: 'ROLE_ADMIN' | 'ROLE_CAIXA';
}

interface JwtPayload {
  exp?: number;
}

const decodeJwtPayload = (token: string): JwtPayload | null => {
  try {
    const base64Url = token.split('.')[1];
    if (!base64Url) return null;
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    const paddedBase64 = base64.padEnd(base64.length + ((4 - (base64.length % 4)) % 4), '=');
    const payload = JSON.parse(atob(paddedBase64));
    return payload as JwtPayload;
  } catch {
    return null;
  }
};

const isTokenExpired = (token: string): boolean => {
  const payload = decodeJwtPayload(token);
  if (!payload?.exp) return false;
  const nowInSeconds = Math.floor(Date.now() / 1000);
  return payload.exp <= nowInSeconds;
};

// Use a separate axios instance for auth (no interceptor auto-unwrap needed)
const authApi = axios.create({
  baseURL: API_BASE_URL,
  timeout: 30000,
  headers: { 'Content-Type': 'application/json' },
});

export const authService = {
  bootstrapSession: (): boolean => {
    const token = localStorage.getItem(STORAGE_KEYS.TOKEN);
    if (!token || isTokenExpired(token)) {
      useUserStore.getState().logout();
      return false;
    }
    useUserStore.getState().setToken(token);
    return true;
  },

  login: async (data: LoginRequest): Promise<string> => {
    const response = await authApi.post<LoginResponse>(ENDPOINTS.AUTH_LOGIN, data);
    const token = response.data.token;
    useUserStore.getState().setToken(token);
    return token;
  },

  register: async (data: RegisterRequest): Promise<void> => {
    await authApi.post(ENDPOINTS.AUTH_REGISTER, data);
  },

  logout: () => {
    useUserStore.getState().logout();
  },

  getToken: (): string | null => {
    const token = localStorage.getItem(STORAGE_KEYS.TOKEN);
    if (!token || isTokenExpired(token)) {
      useUserStore.getState().logout();
      return null;
    }
    return token;
  },

  isAuthenticated: (): boolean => {
    return !!authService.getToken();
  },
};
