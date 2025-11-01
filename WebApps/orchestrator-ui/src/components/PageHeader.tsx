import React from 'react';

interface PageHeaderProps {
  title: string;
  subtitle?: string;
  breadcrumbs?: { label: string; href?: string }[];
  actions?: React.ReactNode;
}

export const PageHeader: React.FC<PageHeaderProps> = ({
  title,
  subtitle,
  breadcrumbs,
  actions,
}) => {
  return (
    <div className="page-header">
      {breadcrumbs && breadcrumbs.length > 0 && (
        <nav aria-label="breadcrumb">
          <ol className="breadcrumb">
            {breadcrumbs.map((breadcrumb, index) => (
              <li
                key={index}
                className={`breadcrumb-item ${
                  index === breadcrumbs.length - 1 ? 'active' : ''
                }`}
              >
                {breadcrumb.href ? (
                  <a href={breadcrumb.href}>{breadcrumb.label}</a>
                ) : (
                  breadcrumb.label
                )}
              </li>
            ))}
          </ol>
        </nav>
      )}
      <div className="d-flex justify-content-between align-items-center mb-3">
        <div>
          <h1>{title}</h1>
          {subtitle && <p className="text-muted mb-0">{subtitle}</p>}
        </div>
        {actions && <div>{actions}</div>}
      </div>
    </div>
  );
};

