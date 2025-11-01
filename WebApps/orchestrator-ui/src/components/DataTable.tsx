import React from 'react';
import { Table } from 'react-bootstrap';

interface Column<T> {
  key: keyof T | string;
  label: string;
  sortable?: boolean;
  render?: (item: T, index: number) => React.ReactNode;
  className?: string;
}

interface DataTableProps<T> {
  data: T[];
  columns: Column<T>[];
  onSort?: (key: string) => void;
  sortBy?: string;
  sortDirection?: 'asc' | 'desc';
  onRowClick?: (item: T, index: number) => void;
  emptyMessage?: string;
  className?: string;
}

export function DataTable<T>({
  data,
  columns,
  onSort,
  sortBy,
  sortDirection = 'asc',
  onRowClick,
  emptyMessage = 'No data available',
  className = '',
}: DataTableProps<T>) {
  const handleSort = (column: Column<T>) => {
    if (column.sortable && onSort) {
      onSort(column.key as string);
    }
  };

  const renderCellValue = (item: T, column: Column<T>, index: number) => {
    if (column.render) {
      return column.render(item, index);
    }
    return String(item[column.key as keyof T] ?? '-');
  };

  return (
    <div className={`table-responsive ${className}`}>
      <Table className="modern-table mb-0">
        <thead>
          <tr>
            {columns.map((column) => (
              <th
                key={String(column.key)}
                onClick={() => handleSort(column)}
                className={column.className}
                style={{ cursor: column.sortable ? 'pointer' : 'default' }}
              >
                {column.label}
                {column.sortable && sortBy === column.key && (
                  <i
                    className={`bi bi-arrow-${sortDirection === 'asc' ? 'up' : 'down'} ms-2`}
                  ></i>
                )}
              </th>
            ))}
          </tr>
        </thead>
        <tbody>
          {data.length === 0 ? (
            <tr>
              <td colSpan={columns.length} className="text-center py-5 text-muted">
                <i className="bi bi-inbox display-4 d-block mb-3"></i>
                {emptyMessage}
              </td>
            </tr>
          ) : (
            data.map((item, index) => (
              <tr
                key={index}
                onClick={() => onRowClick?.(item, index)}
                style={{ cursor: onRowClick ? 'pointer' : 'default' }}
              >
                {columns.map((column) => (
                  <td key={String(column.key)} className={column.className}>
                    {renderCellValue(item, column, index)}
                  </td>
                ))}
              </tr>
            ))
          )}
        </tbody>
      </Table>
    </div>
  );
}

