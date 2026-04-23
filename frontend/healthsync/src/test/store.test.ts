import { describe, it, expect, beforeEach, vi } from 'vitest';
import { act, renderHook } from '@testing-library/react';
import {
  useSidebarStore,
  useThemeStore,
  useToastStore,
  useAppStore,
} from '../store';

describe('useSidebarStore', () => {
  beforeEach(() => {
    const { result } = renderHook(() => useSidebarStore());
    act(() => {
      result.current.setOpen(false);
      result.current.setCollapsed(false);
    });
  });

  it('should have initial state', () => {
    const { result } = renderHook(() => useSidebarStore());
    expect(result.current.isOpen).toBe(false);
    expect(result.current.isCollapsed).toBe(false);
  });

  it('should toggle sidebar open state', () => {
    const { result } = renderHook(() => useSidebarStore());
    
    act(() => {
      result.current.toggle();
    });
    
    expect(result.current.isOpen).toBe(true);
    
    act(() => {
      result.current.toggle();
    });
    
    expect(result.current.isOpen).toBe(false);
  });

  it('should set collapsed state', () => {
    const { result } = renderHook(() => useSidebarStore());
    
    act(() => {
      result.current.setCollapsed(true);
    });
    
    expect(result.current.isCollapsed).toBe(true);
  });
});

describe('useThemeStore', () => {
  it('should have initial theme', () => {
    const { result } = renderHook(() => useThemeStore());
    expect(['light', 'dark', 'system']).toContain(result.current.theme);
  });

  it('should set theme', () => {
    const { result } = renderHook(() => useThemeStore());
    
    act(() => {
      result.current.setTheme('dark');
    });
    
    expect(result.current.theme).toBe('dark');
  });
});

describe('useToastStore', () => {
  beforeEach(() => {
    vi.useFakeTimers();
    const { result } = renderHook(() => useToastStore());
    act(() => {
      // Clear all toasts
      result.current.toasts.forEach((t) => result.current.removeToast(t.id));
    });
  });

  it('should add toast', () => {
    const { result } = renderHook(() => useToastStore());
    
    act(() => {
      result.current.addToast({
        type: 'success',
        title: 'Test',
        message: 'Test message',
      });
    });
    
    expect(result.current.toasts).toHaveLength(1);
    expect(result.current.toasts[0].title).toBe('Test');
  });

  it('should remove toast', () => {
    const { result } = renderHook(() => useToastStore());
    
    let toastId: string;
    act(() => {
      toastId = result.current.addToast({
        type: 'info',
        title: 'Test',
      });
    });
    
    expect(result.current.toasts).toHaveLength(1);
    
    act(() => {
      result.current.removeToast(toastId);
    });
    
    expect(result.current.toasts).toHaveLength(0);
  });

  it('should have helper methods for different toast types', () => {
    const { result } = renderHook(() => useToastStore());
    
    act(() => {
      result.current.success('Success', 'Success message');
    });
    expect(result.current.toasts[0].type).toBe('success');
    
    act(() => {
      result.current.error('Error', 'Error message');
    });
    expect(result.current.toasts[1].type).toBe('error');
    
    act(() => {
      result.current.warning('Warning', 'Warning message');
    });
    expect(result.current.toasts[2].type).toBe('warning');
    
    act(() => {
      result.current.info('Info', 'Info message');
    });
    expect(result.current.toasts[3].type).toBe('info');
  });
});

describe('useAppStore', () => {
  beforeEach(() => {
    const { result } = renderHook(() => useAppStore());
    act(() => {
      result.current.setGlobalLoading(false);
      result.current.setGlobalError(null);
    });
  });

  it('should have initial state', () => {
    const { result } = renderHook(() => useAppStore());
    expect(result.current.globalLoading).toBe(false);
    expect(result.current.globalError).toBeNull();
  });

  it('should set loading state', () => {
    const { result } = renderHook(() => useAppStore());
    
    act(() => {
      result.current.setGlobalLoading(true);
    });
    
    expect(result.current.globalLoading).toBe(true);
  });

  it('should set error', () => {
    const { result } = renderHook(() => useAppStore());
    
    act(() => {
      result.current.setGlobalError('Something went wrong');
    });
    
    expect(result.current.globalError).toBe('Something went wrong');
  });

  it('should clear error', () => {
    const { result } = renderHook(() => useAppStore());
    
    act(() => {
      result.current.setGlobalError('Error');
    });
    
    act(() => {
      result.current.setGlobalError(null);
    });
    
    expect(result.current.globalError).toBeNull();
  });
});
