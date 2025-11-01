// Common type definitions

export type OrchestrationStatus = 'ACTIVE' | 'INACTIVE' | 'PENDING' | 'FAILED';

export type OrchestrationType = 'Sequential' | 'Simultaneous';

export type ExecutionStatus = 'PENDING' | 'RUNNING' | 'SUCCESS' | 'FAILED' | 'CANCELLED';

export type StepStatus = 'PENDING' | 'RUNNING' | 'SUCCESS' | 'FAILED' | 'SKIPPED';

export interface Orchestration {
  orchName: string;
  type: OrchestrationType;
  status: OrchestrationStatus;
  initiatorName?: string;
  initiator?: string;
  registeredWorkersCount?: number;
  totalWorkersExpected?: number;
  lastUpdated?: string;
  createdAt?: string;
}

export interface OrchestrationStep {
  seq: number;
  name: string;
  objectType: string;
  registeredBy: string;
  status: StepStatus;
}

export interface OrchestrationDetails {
  orchName: string;
  type: OrchestrationType;
  status: OrchestrationStatus;
  initiator: string;
  steps: OrchestrationStep[];
}

export interface Execution {
  id: string;
  orchName: string;
  status: ExecutionStatus;
  startedAt: string;
  completedAt?: string;
  duration?: number;
  error?: string;
}

export interface ExecutionDetails extends Execution {
  steps: ExecutionStep[];
}

export interface ExecutionStep {
  seq: number;
  name: string;
  status: StepStatus;
  startedAt?: string;
  completedAt?: string;
  duration?: number;
  error?: string;
  result?: unknown;
}

export interface PaginationParams {
  page: number;
  size: number;
  sortBy: string;
  direction: 'asc' | 'desc';
}

export interface PaginatedResponse<T> {
  content: T[];
  totalPages: number;
  totalElements: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
}

export interface FilterParams {
  status?: string;
  type?: string;
  orchName?: string;
  registeredFrom?: string;
  registeredTo?: string;
  fromDate?: string;
  toDate?: string;
}

export interface ApiError {
  message: string;
  status?: number;
  code?: string;
}

