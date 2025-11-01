import type { ApiError } from '@types';

/**
 * Extract error message from various error types
 */
export function getErrorMessage(error: unknown): string {
  if (typeof error === 'string') return error;

  if (error instanceof Error) return error.message;

  if (typeof error === 'object' && error !== null) {
    const apiError = error as ApiError;
    if (apiError.message) return apiError.message;
  }

  return 'An unexpected error occurred';
}

/**
 * Check if error is a network error
 */
export function isNetworkError(error: unknown): boolean {
  if (error instanceof Error) {
    return error.message.includes('Network') || error.message.includes('fetch');
  }
  return false;
}

/**
 * Check if error is an authentication error
 */
export function isAuthError(error: unknown): boolean {
  if (typeof error === 'object' && error !== null) {
    const apiError = error as ApiError;
    return apiError.status === 401 || apiError.status === 403;
  }
  return false;
}

/**
 * Format error for display
 */
export function formatError(error: unknown): string {
  const message = getErrorMessage(error);

  if (isNetworkError(error)) {
    return `Network Error: ${message}. Please check your connection.`;
  }

  if (isAuthError(error)) {
    return 'Authentication required. Please log in.';
  }

  return message;
}

/**
 * Log error to console in development
 */
export function logError(error: unknown, context?: string): void {
  if (import.meta.env.DEV) {
    console.error(`[Error${context ? ` - ${context}` : ''}]:`, error);
  }
}

