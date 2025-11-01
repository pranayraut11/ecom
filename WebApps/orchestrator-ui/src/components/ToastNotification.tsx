import React from 'react';
import { Toast, ToastContainer } from 'react-bootstrap';
import { useToast } from '@context';

export const ToastNotification: React.FC = () => {
  const { toasts, removeToast } = useToast();

  return (
    <ToastContainer position="top-end" className="p-3" style={{ zIndex: 9999 }}>
      {toasts.map((toast) => (
        <Toast
          key={toast.id}
          onClose={() => removeToast(toast.id)}
          bg={toast.type}
          autohide={toast.duration !== undefined && toast.duration > 0}
          delay={toast.duration}
        >
          <Toast.Header>
            <strong className="me-auto">
              {toast.type === 'success' && <i className="bi bi-check-circle-fill me-2"></i>}
              {toast.type === 'error' && <i className="bi bi-x-circle-fill me-2"></i>}
              {toast.type === 'warning' && <i className="bi bi-exclamation-triangle-fill me-2"></i>}
              {toast.type === 'info' && <i className="bi bi-info-circle-fill me-2"></i>}
              {toast.type.charAt(0).toUpperCase() + toast.type.slice(1)}
            </strong>
          </Toast.Header>
          <Toast.Body className={toast.type === 'error' ? 'text-white' : ''}>
            {toast.message}
          </Toast.Body>
        </Toast>
      ))}
    </ToastContainer>
  );
};

