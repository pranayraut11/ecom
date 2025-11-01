import axios from 'axios';
import type { Orchestration, PaginatedResponse, PaginationParams, FilterParams } from '@types';

const API_BASE_URL = '/api/orchestrations';

interface FetchOrchestrationsParams extends PaginationParams, FilterParams {}

export const fetchOrchestrations = async (
  params: FetchOrchestrationsParams
): Promise<PaginatedResponse<Orchestration>> => {
  try {
    const response = await axios.get<PaginatedResponse<Orchestration>>(API_BASE_URL, { params });
    return response.data;
  } catch (error) {
    const message = error instanceof Error ? error.message : 'Failed to fetch orchestrations';
    throw new Error(message);
  }
};
