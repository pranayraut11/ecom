import axios from 'axios';
import type { Execution, PaginatedResponse, PaginationParams } from '@types';

interface ExecutionParams extends PaginationParams {
  status?: string;
  fromDate?: string;
  toDate?: string;
}

export const fetchExecutions = async (
  orchName: string,
  params: ExecutionParams
): Promise<PaginatedResponse<Execution>> => {
  try {
    const response = await axios.get<PaginatedResponse<Execution>>(
      `/api/orchestrations/${orchName}/executions`,
      { params }
    );
    return response.data;
  } catch (error) {
    const message = error instanceof Error ? error.message : 'Failed to fetch executions';
    throw new Error(message);
  }
};
