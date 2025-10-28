import axios from 'axios';

interface ExecutionParams {
  page: number;
  size: number;
  sortBy: string;
  direction: string;
  status?: string;
  fromDate?: string;
  toDate?: string;
}

export const fetchExecutions = async (orchName: string, params: ExecutionParams) => {
  try {
    const response = await axios.get(`/api/orchestrations/${orchName}/executions`, {
      params: {
        ...params,
      },
    });
    return response.data;
  } catch (error: any) {
    throw new Error(error.response?.data?.message || 'Failed to fetch executions');
  }
};