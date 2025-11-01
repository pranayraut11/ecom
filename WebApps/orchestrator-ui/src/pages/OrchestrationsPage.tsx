import React, { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Button, Form } from 'react-bootstrap';
import { usePagination, useDebounce } from '@hooks';
import type { Orchestration } from '@types';
import {
  PageHeader,
  FilterPanel,
  DataTable,
  LoadingSpinner,
  ErrorAlert,
  StatusBadge,
  PaginationControls,
  SearchBar,
} from '@components';
import { formatDate } from '@utils';
import { fetchOrchestrations } from '@api/orchestrationsApi';

const OrchestrationsPage: React.FC = () => {
  const navigate = useNavigate();
  const [data, setData] = React.useState<Orchestration[]>([]);
  const [loading, setLoading] = React.useState(false);
  const [error, setError] = React.useState<string | null>(null);
  const [sortBy, setSortBy] = React.useState('orchName');
  const [direction, setDirection] = React.useState<'asc' | 'desc'>('asc');
  const [searchTerm, setSearchTerm] = React.useState('');
  const [filters, setFilters] = React.useState({
    status: '',
    type: '',
    registeredFrom: '',
    registeredTo: '',
  });

  const { pagination, setPage, setSize, setTotalPages, setTotalElements } = usePagination(0, 10);
  const debouncedSearch = useDebounce(searchTerm, 300);

  useEffect(() => {
    const loadData = async () => {
      setLoading(true);
      setError(null);
      try {
        const response = await fetchOrchestrations({
          page: pagination.page,
          size: pagination.size,
          sortBy,
          direction,
          orchName: debouncedSearch,
          ...filters,
        });
        setData(response.content || []);
        setTotalPages(response.totalPages || 0);
        setTotalElements(response.totalElements || 0);
      } catch (err) {
        setError(err instanceof Error ? err.message : 'Failed to load orchestrations');
        setData([]);
      } finally {
        setLoading(false);
      }
    };
    loadData();
  }, [pagination.page, pagination.size, sortBy, direction, debouncedSearch, filters]);

  const handleFilterChange = (name: string, value: string) => {
    setFilters((prev) => ({ ...prev, [name]: value }));
    setPage(0);
  };

  const handleClearFilters = () => {
    setFilters({
      status: '',
      type: '',
      registeredFrom: '',
      registeredTo: '',
    });
    setSearchTerm('');
  };

  const handleSort = (key: string) => {
    if (sortBy === key) {
      setDirection((prev) => (prev === 'asc' ? 'desc' : 'asc'));
    } else {
      setSortBy(key);
      setDirection('asc');
    }
  };

  const columns = [
    {
      key: 'orchName',
      label: 'Orchestration Name',
      sortable: true,
      render: (item: Orchestration) => (
        <span className="fw-semibold text-primary">{item.orchName}</span>
      ),
    },
    {
      key: 'type',
      label: 'Type',
      render: (item: Orchestration) => (
        <span className="badge bg-secondary">{item.type}</span>
      ),
    },
    {
      key: 'initiator',
      label: 'Initiator',
      render: (item: Orchestration) => item.initiatorName || item.initiator || '-',
    },
    {
      key: 'workers',
      label: 'Workers',
      render: (item: Orchestration) => (
        <span>
          {item.registeredWorkersCount || 0} / {item.totalWorkersExpected || 0}
        </span>
      ),
    },
    {
      key: 'lastUpdated',
      label: 'Last Updated',
      sortable: true,
      render: (item: Orchestration) => formatDate(item.lastUpdated),
    },
    {
      key: 'status',
      label: 'Status',
      sortable: true,
      render: (item: Orchestration) => <StatusBadge status={item.status} showIcon />,
    },
    {
      key: 'actions',
      label: 'Actions',
      render: (item: Orchestration) => (
        <div className="d-flex gap-2">
          <Button
            variant="primary"
            size="sm"
            onClick={(e) => {
              e.stopPropagation();
              navigate(`/orchestrations/${item.orchName}`);
            }}
          >
            <i className="bi bi-eye me-1"></i>
            Details
          </Button>
          <Button
            variant="outline-primary"
            size="sm"
            onClick={(e) => {
              e.stopPropagation();
              navigate(`/executions?orchName=${encodeURIComponent(item.orchName)}`);
            }}
          >
            <i className="bi bi-list-check me-1"></i>
            Executions
          </Button>
        </div>
      ),
    },
  ];

  const filterConfigs = [
    {
      name: 'type',
      label: 'Type',
      type: 'select' as const,
      options: [
        { value: 'Sequential', label: 'Sequential' },
        { value: 'Simultaneous', label: 'Simultaneous' },
      ],
    },
    {
      name: 'status',
      label: 'Status',
      type: 'select' as const,
      options: [
        { value: 'ACTIVE', label: 'Active' },
        { value: 'INACTIVE', label: 'Inactive' },
        { value: 'PENDING', label: 'Pending' },
        { value: 'FAILED', label: 'Failed' },
      ],
    },
    {
      name: 'registeredFrom',
      label: 'Registered From',
      type: 'date' as const,
    },
    {
      name: 'registeredTo',
      label: 'Registered To',
      type: 'date' as const,
    },
  ];

  return (
    <div className="container-fluid py-4">
      <PageHeader
        title="Orchestrations"
        subtitle={`Manage and monitor your orchestration workflows (${pagination.totalElements} total)`}
        breadcrumbs={[
          { label: 'Home', href: '/' },
          { label: 'Orchestrations' },
        ]}
      />

      <div className="mb-3">
        <SearchBar
          value={searchTerm}
          onChange={setSearchTerm}
          placeholder="Search orchestrations by name..."
        />
      </div>

      <FilterPanel
        filters={filters}
        filterConfigs={filterConfigs}
        onFilterChange={handleFilterChange}
        onClearFilters={handleClearFilters}
        collapsible
      />

      {error && <ErrorAlert error={error} />}

      {loading ? (
        <LoadingSpinner fullPage message="Loading orchestrations..." />
      ) : (
        <>
          <DataTable
            data={data}
            columns={columns}
            onSort={handleSort}
            sortBy={sortBy}
            sortDirection={direction}
            onRowClick={(item) => navigate(`/orchestrations/${item.orchName}`)}
            emptyMessage="No orchestrations found. Try adjusting your filters."
          />

          {pagination.totalPages > 1 && (
            <div className="d-flex justify-content-between align-items-center mt-4">
              <div className="d-flex align-items-center gap-2">
                <span className="text-muted small">Rows per page:</span>
                <Form.Select
                  size="sm"
                  value={pagination.size}
                  onChange={(e) => setSize(Number(e.target.value))}
                  style={{ width: 'auto' }}
                >
                  <option value="10">10</option>
                  <option value="25">25</option>
                  <option value="50">50</option>
                  <option value="100">100</option>
                </Form.Select>
              </div>

              <PaginationControls
                currentPage={pagination.page}
                totalPages={pagination.totalPages}
                onPageChange={setPage}
              />

              <div className="text-muted small">
                Showing {pagination.page * pagination.size + 1} to{' '}
                {Math.min((pagination.page + 1) * pagination.size, pagination.totalElements)} of{' '}
                {pagination.totalElements} results
              </div>
            </div>
          )}
        </>
      )}
    </div>
  );
};

export default OrchestrationsPage;

