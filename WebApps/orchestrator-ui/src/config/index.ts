/**
 * Environment configuration utilities
 */

export const config = {
  api: {
    baseUrl: import.meta.env.VITE_API_BASE_URL || '/api',
  },
  app: {
    name: import.meta.env.VITE_APP_NAME || 'Orchestrator Dashboard',
    version: import.meta.env.VITE_APP_VERSION || '0.1.0',
  },
  features: {
    enableMockApi: import.meta.env.VITE_ENABLE_MOCK_API === 'true',
    enableDebug: import.meta.env.VITE_ENABLE_DEBUG === 'true',
  },
} as const;

export default config;

