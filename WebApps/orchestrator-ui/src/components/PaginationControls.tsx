import React from 'react';
import { Pagination as BootstrapPagination } from 'react-bootstrap';

interface PaginationControlsProps {
  currentPage: number;
  totalPages: number;
  onPageChange: (page: number) => void;
  maxVisible?: number;
}

export const PaginationControls: React.FC<PaginationControlsProps> = ({
  currentPage,
  totalPages,
  onPageChange,
  maxVisible = 5,
}) => {
  if (totalPages <= 1) return null;

  const getPageNumbers = (): number[] => {
    const pages: number[] = [];
    const halfVisible = Math.floor(maxVisible / 2);
    let startPage = Math.max(0, currentPage - halfVisible);
    let endPage = Math.min(totalPages - 1, currentPage + halfVisible);

    if (currentPage - halfVisible < 0) {
      endPage = Math.min(totalPages - 1, endPage + (halfVisible - currentPage));
    }

    if (currentPage + halfVisible >= totalPages) {
      startPage = Math.max(0, startPage - (currentPage + halfVisible - totalPages + 1));
    }

    for (let i = startPage; i <= endPage; i++) {
      pages.push(i);
    }

    return pages;
  };

  const pageNumbers = getPageNumbers();

  return (
    <BootstrapPagination className="mb-0">
      <BootstrapPagination.First
        onClick={() => onPageChange(0)}
        disabled={currentPage === 0}
      />
      <BootstrapPagination.Prev
        onClick={() => onPageChange(currentPage - 1)}
        disabled={currentPage === 0}
      />

      {pageNumbers[0] > 0 && (
        <>
          <BootstrapPagination.Item onClick={() => onPageChange(0)}>1</BootstrapPagination.Item>
          {pageNumbers[0] > 1 && <BootstrapPagination.Ellipsis disabled />}
        </>
      )}

      {pageNumbers.map((page) => (
        <BootstrapPagination.Item
          key={page}
          active={page === currentPage}
          onClick={() => onPageChange(page)}
        >
          {page + 1}
        </BootstrapPagination.Item>
      ))}

      {pageNumbers[pageNumbers.length - 1] < totalPages - 1 && (
        <>
          {pageNumbers[pageNumbers.length - 1] < totalPages - 2 && (
            <BootstrapPagination.Ellipsis disabled />
          )}
          <BootstrapPagination.Item onClick={() => onPageChange(totalPages - 1)}>
            {totalPages}
          </BootstrapPagination.Item>
        </>
      )}

      <BootstrapPagination.Next
        onClick={() => onPageChange(currentPage + 1)}
        disabled={currentPage === totalPages - 1}
      />
      <BootstrapPagination.Last
        onClick={() => onPageChange(totalPages - 1)}
        disabled={currentPage === totalPages - 1}
      />
    </BootstrapPagination>
  );
};

