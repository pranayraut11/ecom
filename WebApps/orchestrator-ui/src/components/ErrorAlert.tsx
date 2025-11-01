import React from 'react';
import { Alert } from 'react-bootstrap';

interface ErrorAlertProps {
  error: string | null;
  onDismiss?: () => void;
  dismissible?: boolean;
}

export const ErrorAlert: React.FC<ErrorAlertProps> = ({
  error,
  onDismiss,
  dismissible = true
}) => {
  if (!error) return null;

  return (
    <Alert variant="danger" dismissible={dismissible} onClose={onDismiss}>
      <Alert.Heading>
        <i className="bi bi-exclamation-triangle-fill me-2"></i>
        Error
      </Alert.Heading>
      <p className="mb-0">{error}</p>
    </Alert>
  );
};

