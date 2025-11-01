import axios from 'axios';
import type { OrchestrationDetails } from '@types';

export const fetchOrchestrationDetails = async (
  orchName: string
): Promise<OrchestrationDetails> => {
  try {
    const response = await axios.get<OrchestrationDetails>(`/api/orchestrations/${orchName}`);
    return response.data;
  } catch (error) {
    const message = error instanceof Error ? error.message : 'Failed to fetch orchestration details';
    throw new Error(message);
  }
};
