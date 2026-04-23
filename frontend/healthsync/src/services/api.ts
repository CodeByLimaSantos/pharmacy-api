import axios from 'axios';
import type { AxiosError, AxiosInstance, AxiosResponse } from 'axios';
import { API_BASE_URL, API_TIMEOUT } from '../constants';
import type { ApiError } from '../types';
import { authService } from './authService';

// Create axios instance
const api: AxiosInstance = axios.create({
  baseURL: API_BASE_URL,
  timeout: API_TIMEOUT,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor — attach JWT Bearer token
api.interceptors.request.use(
  (config) => {
    const token = authService.getToken();
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    } else if (config.headers?.Authorization) {
      delete config.headers.Authorization;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor — auto-unwrap ApiResponse wrapper and handle errors
api.interceptors.response.use(
  (response: AxiosResponse) => {
    // Backend wraps all responses in ApiResponse { success, message, code, data, ... }
    // Auto-unwrap: extract the inner `data` so services get the payload directly
    const body = response.data;
    if (body && typeof body === 'object' && 'success' in body && 'data' in body) {
      response.data = body.data;
    }
    return response;
  },
  (error: AxiosError<ApiError>) => {
    if (error.response) {
      const { status, data } = error.response;

      switch (status) {
        case 401:
          // Unauthorized — clear token and redirect to login
          authService.logout();
          if (window.location.pathname !== '/login') {
            window.location.href = '/login';
          }
          break;
        case 403:
          console.error('Access forbidden — insufficient permissions');
          break;
        case 404:
          console.error('Resource not found');
          break;
        case 409:
          console.error('Conflict:', data?.message);
          break;
        case 500:
          console.error('Server error');
          break;
        default:
          console.error('API error:', data?.message || error.message);
      }
    } else if (error.request) {
      console.error('Network error — no response received');
    } else {
      console.error('Request error:', error.message);
    }

    return Promise.reject(error);
  }
);

export default api;

// Export helper functions for common operations
export const get = <T>(url: string, params?: object): Promise<AxiosResponse<T>> =>
  api.get<T>(url, { params });

export const post = <T>(url: string, data?: object): Promise<AxiosResponse<T>> =>
  api.post<T>(url, data);

export const put = <T>(url: string, data?: object): Promise<AxiosResponse<T>> =>
  api.put<T>(url, data);

export const del = <T>(url: string): Promise<AxiosResponse<T>> =>
  api.delete<T>(url);

export const patch = <T>(url: string, data?: object): Promise<AxiosResponse<T>> =>
  api.patch<T>(url, data);
