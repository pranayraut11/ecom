import React from 'react';
import { Card } from 'react-bootstrap';

interface StatsCardProps {
  icon: string;
  value: string | number;
  label: string;
  trend?: {
    value: number;
    direction: 'up' | 'down';
  };
  color?: 'primary' | 'success' | 'danger' | 'warning' | 'info';
  onClick?: () => void;
}

export const StatsCard: React.FC<StatsCardProps> = ({
  icon,
  value,
  label,
  trend,
  color = 'primary',
  onClick,
}) => {
  const colorClasses = {
    primary: 'bg-primary-subtle text-primary',
    success: 'bg-success-subtle text-success',
    danger: 'bg-danger-subtle text-danger',
    warning: 'bg-warning-subtle text-warning',
    info: 'bg-info-subtle text-info',
  };

  return (
    <Card
      className="stats-card fade-in"
      style={{ cursor: onClick ? 'pointer' : 'default' }}
      onClick={onClick}
    >
      <Card.Body>
        <div className={`stats-icon ${colorClasses[color]}`}>
          <i className={icon}></i>
        </div>
        <div className="stats-value">{value}</div>
        <div className="d-flex justify-content-between align-items-center">
          <div className="stats-label">{label}</div>
          {trend && (
            <div
              className={`small fw-semibold ${
                trend.direction === 'up' ? 'text-success' : 'text-danger'
              }`}
            >
              <i className={`bi bi-arrow-${trend.direction} me-1`}></i>
              {Math.abs(trend.value)}%
            </div>
          )}
        </div>
      </Card.Body>
    </Card>
  );
};

