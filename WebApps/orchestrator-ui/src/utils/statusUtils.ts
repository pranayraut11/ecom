import type { OrchestrationStatus, ExecutionStatus, StepStatus } from '@types';
import { STATUS_COLORS } from '@/constants';

/**
 * Get Bootstrap variant for a status
 */
export function getStatusVariant(
  status: OrchestrationStatus | ExecutionStatus | StepStatus | string
): string {
  return STATUS_COLORS[status as keyof typeof STATUS_COLORS] || 'secondary';
}

/**
 * Get CSS class for status badge
 */
export function getStatusClass(
  status: OrchestrationStatus | ExecutionStatus | StepStatus | string
): string {
  const variant = getStatusVariant(status);
  return `badge bg-${variant}`;
}

/**
 * Check if status is a success state
 */
export function isSuccessStatus(status: string): boolean {
  return status === 'SUCCESS' || status === 'ACTIVE' || status === 'COMPLETED';
}

/**
 * Check if status is a failure state
 */
export function isFailureStatus(status: string): boolean {
  return status === 'FAILED' || status === 'ERROR';
}

/**
 * Check if status is a pending/in-progress state
 */
export function isPendingStatus(status: string): boolean {
  return status === 'PENDING' || status === 'RUNNING' || status === 'IN_PROGRESS';
}

/**
 * Get status icon class
 */
export function getStatusIcon(status: string): string {
  if (isSuccessStatus(status)) return 'bi bi-check-circle-fill';
  if (isFailureStatus(status)) return 'bi bi-x-circle-fill';
  if (isPendingStatus(status)) return 'bi bi-clock-fill';
  return 'bi bi-circle-fill';
}

/**
 * Format status text for display
 */
export function formatStatus(status: string): string {
  return status
    .split('_')
    .map((word) => word.charAt(0).toUpperCase() + word.slice(1).toLowerCase())
    .join(' ');
}

