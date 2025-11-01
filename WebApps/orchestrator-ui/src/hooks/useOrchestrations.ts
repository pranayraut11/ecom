import { useState, useCallback } from 'react';
import { fetchOrchestrations } from '@api/orchestrationsApi';
import type { Orchestration, PaginatedResponse } from '@types';

interface OrchestrationFilters {
  status?: string;
  type?: string;
  orchName?: string;
  registeredFrom?: string;
  registeredTo?: string;
}

interface UseOrchestrationsParams {
  page: number;
  size: number;
  sortBy: string;
  direction: string;
  filters?: OrchestrationFilters;
}

interface UseOrchestrationsResult {
  data: Orchestration[];
  loading: boolean;
  error: string | null;
  totalPages: number;
  totalElements: number;
  refetch: () => Promise<void>;
}

/**
 * Custom hook for fetching orchestrations with pagination and filtering
 */
export function useOrchestrations(
  params: UseOrchestrationsParams
): UseOrchestrationsResult {
  const [data, setData] = useState<Orchestration[]>([]);
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);
  const [totalPages, setTotalPages] = useState<number>(0);
  const [totalElements, setTotalElements] = useState<number>(0);

  const fetchData = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const response: PaginatedResponse<Orchestration> = await fetchOrchestrations({
        ...params,
        ...params.filters,
      });
      setData(response.content || []);
      setTotalPages(response.totalPages || 0);
      setTotalElements(response.totalElements || 0);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to fetch orchestrations');
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

