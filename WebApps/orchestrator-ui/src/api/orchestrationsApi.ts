import axios from 'axios';

const API_BASE_URL = '/api/orchestrations';

export const fetchOrchestrations = async (params: {
  page: number;
  size: number;
  sortBy: string;
  direction: string;
  status?: string;
  type?: string;
  orchName?: string;
  registeredFrom?: string;
  registeredTo?: string;
}) => {
  try {
    const response = await axios.get(API_BASE_URL, { params });
    return response.data;
  } catch (error: any) {
    throw new Error(error.response?.data?.message || 'Failed to fetch orchestrations');
  }
};