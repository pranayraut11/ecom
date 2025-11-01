import React from 'react';
import { Badge } from 'react-bootstrap';
import { getStatusVariant, formatStatus } from '@utils/statusUtils';
import type { OrchestrationStatus, ExecutionStatus, StepStatus } from '@types';

interface StatusBadgeProps {
  status: OrchestrationStatus | ExecutionStatus | StepStatus | string;
  showIcon?: boolean;
  className?: string;
}

export const StatusBadge: React.FC<StatusBadgeProps> = ({
  status,
  showIcon = false,
  className = '',
}) => {
  const variant = getStatusVariant(status);
  const formattedStatus = formatStatus(status);

  return (
    <Badge bg={variant} className={className}>
      {showIcon && <i className={`bi bi-circle-fill me-1`} style={{ fontSize: '0.5rem' }}></i>}
      {formattedStatus}
    </Badge>
  );
};

