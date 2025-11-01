import React from 'react';
import { Form } from 'react-bootstrap';

interface SearchBarProps {
  value: string;
  onChange: (value: string) => void;
  placeholder?: string;
  className?: string;
}

export const SearchBar: React.FC<SearchBarProps> = ({
  value,
  onChange,
  placeholder = 'Search...',
  className = '',
}) => {
  return (
    <div className={`position-relative ${className}`}>
      <i
        className="bi bi-search position-absolute top-50 translate-middle-y ms-3"
        style={{ pointerEvents: 'none' }}
      ></i>
      <Form.Control
        type="text"
        placeholder={placeholder}
        value={value}
        onChange={(e) => onChange(e.target.value)}
        style={{ paddingLeft: '2.5rem' }}
      />
      {value && (
        <i
          className="bi bi-x-circle-fill position-absolute top-50 translate-middle-y end-0 me-3 cursor-pointer text-muted"
          onClick={() => onChange('')}
          style={{ cursor: 'pointer' }}
        ></i>
      )}
    </div>
  );
};

