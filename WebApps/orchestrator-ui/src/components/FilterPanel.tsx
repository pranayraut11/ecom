import React from 'react';
import { Form, Row, Col, Button } from 'react-bootstrap';

interface FilterConfig {
  name: string;
  label: string;
  type: 'text' | 'select' | 'date';
  options?: { value: string; label: string }[];
  placeholder?: string;
}

interface FilterPanelProps {
  filters: Record<string, string>;
  filterConfigs: FilterConfig[];
  onFilterChange: (name: string, value: string) => void;
  onClearFilters: () => void;
  collapsible?: boolean;
}

export const FilterPanel: React.FC<FilterPanelProps> = ({
  filters,
  filterConfigs,
  onFilterChange,
  onClearFilters,
  collapsible = false,
}) => {
  const [collapsed, setCollapsed] = React.useState(collapsible);

  const hasActiveFilters = Object.values(filters).some((value) => value !== '');

  return (
    <div className="filter-panel">
      <div className="filter-header">
        <h5>
          <i className="bi bi-funnel me-2"></i>
          Filters
          {hasActiveFilters && (
            <span className="badge bg-primary ms-2">
              {Object.values(filters).filter((v) => v).length}
            </span>
          )}
        </h5>
        <div className="d-flex gap-2">
          {hasActiveFilters && (
            <Button variant="outline-secondary" size="sm" onClick={onClearFilters}>
              <i className="bi bi-x-circle me-1"></i>
              Clear All
            </Button>
          )}
          {collapsible && (
            <Button
              variant="link"
              size="sm"
              onClick={() => setCollapsed(!collapsed)}
              className="text-decoration-none"
            >
              <i className={`bi bi-chevron-${collapsed ? 'down' : 'up'}`}></i>
            </Button>
          )}
        </div>
      </div>

      {!collapsed && (
        <div className="filter-grid">
          {filterConfigs.map((config) => (
            <Form.Group key={config.name} controlId={`filter-${config.name}`}>
              <Form.Label className="small fw-semibold text-muted">
                {config.label}
              </Form.Label>
              {config.type === 'select' ? (
                <Form.Select
                  size="sm"
                  name={config.name}
                  value={filters[config.name] || ''}
                  onChange={(e) => onFilterChange(config.name, e.target.value)}
                >
                  <option value="">All</option>
                  {config.options?.map((option) => (
                    <option key={option.value} value={option.value}>
                      {option.label}
                    </option>
                  ))}
                </Form.Select>
              ) : (
                <Form.Control
                  size="sm"
                  type={config.type}
                  name={config.name}
                  value={filters[config.name] || ''}
                  onChange={(e) => onFilterChange(config.name, e.target.value)}
                  placeholder={config.placeholder}
                />
              )}
            </Form.Group>
          ))}
        </div>
      )}
    </div>
  );
};

