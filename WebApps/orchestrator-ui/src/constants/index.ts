/**
 * Constants used throughout the application
 */

export const APP_NAME = 'Orchestrator Dashboard';

export const DEFAULT_PAGE_SIZE = 10;
export const PAGE_SIZE_OPTIONS = [10, 25, 50, 100];

export const DEBOUNCE_DELAY = 300; // milliseconds

export const DATE_FORMAT = 'YYYY-MM-DD HH:mm:ss';
export const DATE_FORMAT_SHORT = 'YYYY-MM-DD';

export const STATUS_COLORS = {
  SUCCESS: 'success',
  RUNNING: 'info',
  PENDING: 'warning',
  FAILED: 'danger',
  CANCELLED: 'secondary',
  ROLLED_BACK: 'warning',
  ACTIVE: 'success',
  INACTIVE: 'secondary',
  SKIPPED: 'secondary',
} as const;

export const ROUTES = {
  HOME: '/',
  DASHBOARD: '/',
  ORCHESTRATIONS: '/orchestrations-page',
  ORCHESTRATION_DETAILS: '/orchestrations/:orchName',
  ORCHESTRATION_EXECUTIONS: '/orchestrations/:orchName/executions',
  EXECUTIONS: '/executions',
  SELF_HEALING: '/self-healing',
} as const;

export const API_ENDPOINTS = {
  ORCHESTRATIONS: '/orchestrations',
  EXECUTIONS: '/executions',
} as const;
// Type definitions for environment variables
interface ImportMetaEnv {
  readonly VITE_API_BASE_URL: string;
  readonly VITE_APP_NAME: string;
  readonly VITE_APP_VERSION: string;
  readonly VITE_ENABLE_MOCK_API: string;
  readonly VITE_ENABLE_DEBUG: string;
}

interface ImportMeta {
  readonly env: ImportMetaEnv;
}

