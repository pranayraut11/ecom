import React from 'react';
import { Spinner } from 'react-bootstrap';

interface LoadingSpinnerProps {
  message?: string;
  size?: 'sm' | 'lg';
  fullPage?: boolean;
}

export const LoadingSpinner: React.FC<LoadingSpinnerProps> = ({
  message = 'Loading...',
  size,
  fullPage = false,
}) => {
  const spinner = (
    <div className="text-center">
      <Spinner animation="border" role="status" size={size} className="mb-2">
        <span className="visually-hidden">Loading...</span>
      </Spinner>
      {message && <p className="text-muted">{message}</p>}
    </div>
  );

  if (fullPage) {
    return (
      <div
        className="d-flex align-items-center justify-content-center"
        style={{ minHeight: '400px' }}
      >
        {spinner}
      </div>
    );
  }

  return spinner;
};

