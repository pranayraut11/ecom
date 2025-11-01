import { useState, useCallback } from 'react';

export interface PaginationState {
  page: number;
  size: number;
  totalPages: number;
  totalElements: number;
}

interface UsePaginationResult {
  pagination: PaginationState;
  setPage: (page: number) => void;
  setSize: (size: number) => void;
  setTotalPages: (totalPages: number) => void;
  setTotalElements: (totalElements: number) => void;
  nextPage: () => void;
  prevPage: () => void;
  goToFirstPage: () => void;
  goToLastPage: () => void;
  canGoNext: boolean;
  canGoPrev: boolean;
}

/**
 * Custom hook for managing pagination state
 * @param initialPage - Initial page number (default: 0)
 * @param initialSize - Initial page size (default: 10)
 */
export function usePagination(
  initialPage: number = 0,
  initialSize: number = 10
): UsePaginationResult {
  const [pagination, setPagination] = useState<PaginationState>({
    page: initialPage,
    size: initialSize,
    totalPages: 0,
    totalElements: 0,
  });

  const setPage = useCallback((page: number) => {
    setPagination((prev) => ({ ...prev, page }));
  }, []);

  const setSize = useCallback((size: number) => {
    setPagination((prev) => ({ ...prev, size, page: 0 }));
  }, []);

  const setTotalPages = useCallback((totalPages: number) => {
    setPagination((prev) => ({ ...prev, totalPages }));
  }, []);

  const setTotalElements = useCallback((totalElements: number) => {
    setPagination((prev) => ({ ...prev, totalElements }));
  }, []);

  const nextPage = useCallback(() => {
    setPagination((prev) => ({
      ...prev,
      page: Math.min(prev.page + 1, prev.totalPages - 1),
    }));
  }, []);

  const prevPage = useCallback(() => {
    setPagination((prev) => ({
      ...prev,
      page: Math.max(prev.page - 1, 0),
    }));
  }, []);

  const goToFirstPage = useCallback(() => {
    setPage(0);
  }, [setPage]);

  const goToLastPage = useCallback(() => {
    setPagination((prev) => ({ ...prev, page: prev.totalPages - 1 }));
  }, []);

  const canGoNext = pagination.page < pagination.totalPages - 1;
  const canGoPrev = pagination.page > 0;

  return {
    pagination,
    setPage,
    setSize,
    setTotalPages,
    setTotalElements,
    nextPage,
    prevPage,
    goToFirstPage,
    goToLastPage,
    canGoNext,
    canGoPrev,
  };
}

