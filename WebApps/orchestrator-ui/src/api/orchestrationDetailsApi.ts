import axios from 'axios';

export const fetchOrchestrationDetails = async (orchName: string) => {
  try {
    const response = await axios.get(`/api/orchestrations/${orchName}`);
    return response.data;
  } catch (error: any) {
    throw new Error(error.response?.data?.message || 'Failed to fetch orchestration details');
  }
};