import { useState, useCallback } from 'react';
import { fetchExecutions } from '@api/executionsApi';
import type { Execution, PaginatedResponse } from '@types';

interface ExecutionFilters {
  status?: string;
  fromDate?: string;
  toDate?: string;
}

interface UseExecutionsParams {
  orchName: string;
  page: number;
  size: number;
  sortBy: string;
  direction: string;
  filters?: ExecutionFilters;
}

interface UseExecutionsResult {
  data: Execution[];
  loading: boolean;
  error: string | null;
  totalPages: number;
  totalElements: number;
  refetch: () => Promise<void>;
}

/**
 * Custom hook for fetching executions with pagination and filtering
 */
export function useExecutions(params: UseExecutionsParams): UseExecutionsResult {
  const [data, setData] = useState<Execution[]>([]);
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);
  const [totalPages, setTotalPages] = useState<number>(0);
  const [totalElements, setTotalElements] = useState<number>(0);

  const fetchData = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const response: PaginatedResponse<Execution> = await fetchExecutions(params.orchName, {
        page: params.page,
        size: params.size,
        sortBy: params.sortBy,
        direction: params.direction,
        ...params.filters,
      });
      setData(response.content || []);
      setTotalPages(response.totalPages || 0);
      setTotalElements(response.totalElements || 0);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to fetch executions');
      setData([]);
    } finally {
      setLoading(false);
    }
  }, [params]);

  const refetch = useCallback(async () => {
    await fetchData();
  }, [fetchData]);

  return {
    data,
    loading,
    error,
    totalPages,
    totalElements,
    refetch,
  };
}

