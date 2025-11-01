import React from 'react';

interface EmptyStateProps {
  icon?: string;
  title: string;
  description?: string;
  action?: {
    label: string;
    onClick: () => void;
  };
}

export const EmptyState: React.FC<EmptyStateProps> = ({ icon, title, description, action }) => {
  return (
    <div className="text-center py-5">
      {icon && <i className={`${icon} display-1 text-muted mb-3`}></i>}
      <h4 className="text-muted">{title}</h4>
      {description && <p className="text-muted">{description}</p>}
      {action && (
        <button className="btn btn-primary mt-3" onClick={action.onClick}>
          {action.label}
        </button>
      )}
    </div>
  );
};

