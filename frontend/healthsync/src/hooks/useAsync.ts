import { useState, useEffect, useCallback } from 'react';
import type { LoadingState } from '../types';
import { parseApiError } from '../utils';

interface UseAsyncState<T> {
  data: T | null;
  loading: boolean;
  error: string | null;
  status: LoadingState;
}

interface UseAsyncReturn<T> extends UseAsyncState<T> {
  execute: () => Promise<void>;
  reset: () => void;
  setData: (data: T | null) => void;
}

/**
 * Custom hook for async operations with loading/error states
 */
export function useAsync<T>(
  asyncFn: () => Promise<T>,
  immediate: boolean = true
): UseAsyncReturn<T> {
  const [state, setState] = useState<UseAsyncState<T>>({
    data: null,
    loading: immediate,
    error: null,
    status: immediate ? 'loading' : 'idle',
  });

  const execute = useCallback(async () => {
    setState((prev) => ({ ...prev, loading: true, error: null, status: 'loading' }));

    try {
      const data = await asyncFn();
      setState({ data, loading: false, error: null, status: 'success' });
    } catch (err) {
      const errorMessage = parseApiError(err);
      setState((prev) => ({ ...prev, loading: false, error: errorMessage, status: 'error' }));
    }
  }, [asyncFn]);

  const reset = useCallback(() => {
    setState({ data: null, loading: false, error: null, status: 'idle' });
  }, []);

  const setData = useCallback((data: T | null) => {
    setState((prev) => ({ ...prev, data }));
  }, []);

  useEffect(() => {
    if (immediate) {
      execute();
    }
  }, [execute, immediate]);

  return { ...state, execute, reset, setData };
}

/**
 * Custom hook for form submission with loading state
 */
export function useSubmit<T, R>(
  submitFn: (data: T) => Promise<R>
): {
  submit: (data: T) => Promise<R | null>;
  loading: boolean;
  error: string | null;
  reset: () => void;
} {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const submit = useCallback(
    async (data: T): Promise<R | null> => {
      setLoading(true);
      setError(null);

      try {
        const result = await submitFn(data);
        setLoading(false);
        return result;
      } catch (err) {
        const errorMessage = parseApiError(err);
        setError(errorMessage);
        setLoading(false);
        return null;
      }
    },
    [submitFn]
  );

  const reset = useCallback(() => {
    setLoading(false);
    setError(null);
  }, []);

  return { submit, loading, error, reset };
}
