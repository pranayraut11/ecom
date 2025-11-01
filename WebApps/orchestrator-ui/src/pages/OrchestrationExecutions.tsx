import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Container, Row, Col, Button } from 'react-bootstrap';
import {
  PageHeader,
  SearchBar,
  FilterPanel,
  DataTable,
  PaginationControls,
  LoadingSpinner,
  ErrorAlert,
  EmptyState,
  StatusBadge,
  StatsCard,
} from '@components';
import { usePagination, useDebounce } from '@hooks';
import { fetchExecutions } from '@api';
import type { Execution } from '@types';
import { formatDate } from '@utils';

interface ExecutionWithDetails extends Execution {
  executionId: string;
  initiator: string;
  executedSteps: number;
  failedSteps: number;
  startTime: string;
  endTime?: string;
}

const OrchestrationExecutions: React.FC = () => {
  const { orchName } = useParams<{ orchName: string }>();
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [executions, setExecutions] = useState<ExecutionWithDetails[]>([]);
  const [totalElements, setTotalElements] = useState(0);
  const [searchTerm, setSearchTerm] = useState('');
  const [filters, setFilters] = useState({
    status: '',
    fromDate: '',
    toDate: '',
  });

  const {
    currentPage,
    pageSize,
    totalPages,
    setTotalPages,
    handlePageChange,
    handlePageSizeChange,
    resetPagination,
  } = usePagination();

  const debouncedSearch = useDebounce(searchTerm, 300);

  const loadExecutions = async () => {
    if (!orchName) return;

    setLoading(true);
    setError(null);
    try {
      const params = {
        page: currentPage,
        size: pageSize,
        sortBy: 'startTime',
        direction: 'desc' as const,
        status: filters.status,
        fromDate: filters.fromDate,
        toDate: filters.toDate,
      };

      const result = await fetchExecutions(orchName, params);
      setExecutions(result.content as any);
      setTotalElements(result.totalElements);
      setTotalPages(result.totalPages);
    } catch (err: any) {
      setError(err.message || 'Failed to load executions');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadExecutions();
  }, [currentPage, pageSize, debouncedSearch, filters, orchName]);

  const handleFilterChange = (name: string, value: string) => {
    setFilters(prev => ({ ...prev, [name]: value }));
    resetPagination();
  };

  const handleClearFilters = () => {
    setFilters({
      status: '',
      fromDate: '',
      toDate: '',
    });
    setSearchTerm('');
    resetPagination();
  };

  const handleRowClick = (execution: ExecutionWithDetails) => {
    navigate(`/executions/${execution.executionId}`);
  };

  // Filter configurations
  const filterConfigs = [
    {
      name: 'status',
      label: 'Status',
      type: 'select' as const,
      options: [
        { value: '', label: 'All Statuses' },
        { value: 'PENDING', label: 'Pending' },
        { value: 'RUNNING', label: 'Running' },
        { value: 'SUCCESS', label: 'Success' },
        { value: 'FAILED', label: 'Failed' },
      ],
    },
    {
      name: 'fromDate',
      label: 'From Date',
      type: 'datetime-local' as const,
    },
    {
      name: 'toDate',
      label: 'To Date',
      type: 'datetime-local' as const,
    },
  ];

  // Table columns
  const columns = [
    {
      key: 'executionId',
      label: 'Execution ID',
      sortable: true,
      render: (item: ExecutionWithDetails) => (
        <span className="text-primary fw-semibold">
          {item.executionId.substring(0, 8)}...
        </span>
      ),
    },
    {
      key: 'status',
      label: 'Status',
      sortable: true,
      render: (item: ExecutionWithDetails) => <StatusBadge status={item.status} showIcon />,
    },
    {
      key: 'initiator',
      label: 'Initiator',
      sortable: true,
    },
    {
      key: 'startTime',
      label: 'Started At',
      sortable: true,
      render: (item: ExecutionWithDetails) => formatDate(item.startTime),
    },
    {
      key: 'endTime',
      label: 'Completed At',
      sortable: true,
      render: (item: ExecutionWithDetails) =>
        item.endTime ? formatDate(item.endTime) : <span className="text-muted">Running...</span>,
    },
    {
      key: 'executedSteps',
      label: 'Executed Steps',
      render: (item: ExecutionWithDetails) => (
        <span className="badge bg-info">{item.executedSteps}</span>
      ),
    },
    {
      key: 'failedSteps',
      label: 'Failed Steps',
      render: (item: ExecutionWithDetails) => (
        <span className={`badge ${item.failedSteps > 0 ? 'bg-danger' : 'bg-secondary'}`}>
          {item.failedSteps}
        </span>
      ),
    },
    {
      key: 'actions',
      label: 'Actions',
      render: (item: ExecutionWithDetails) => (
        <Button
          variant="outline-primary"
          size="sm"
          onClick={(e) => {
            e.stopPropagation();
            navigate(`/executions/${item.executionId}`);
          }}
        >
          <i className="bi bi-eye me-1"></i>
          Details
        </Button>
      ),
    },
  ];

  const breadcrumbs = [
    { label: 'Home', href: '/' },
    { label: 'Orchestrations', href: '/orchestrations' },
    { label: orchName || 'Unknown' },
  ];

  // Calculate stats
  const stats = {
    total: totalElements,
    running: executions.filter(e => e.status === 'RUNNING').length,
    success: executions.filter(e => e.status === 'SUCCESS').length,
    failed: executions.filter(e => e.status === 'FAILED').length,
    successRate: totalElements > 0
      ? ((executions.filter(e => e.status === 'SUCCESS').length / executions.length) * 100).toFixed(1)
      : '0',
  };

  if (loading && executions.length === 0) {
    return <LoadingSpinner message="Loading execution history..." />;
  }

  return (
    <Container fluid className="py-4">
      <PageHeader
        title={`Execution History`}
        subtitle={`${orchName} - View all execution instances`}
        breadcrumbs={breadcrumbs}
        actions={
          <Button variant="outline-secondary" onClick={() => navigate('/orchestrations')}>
            <i className="bi bi-arrow-left me-2"></i>
            Back to Orchestrations
          </Button>
        }
      />

      {/* Stats Cards */}
      <Row className="mb-4">
        <Col md={3}>
          <StatsCard
            icon="bi bi-play-circle"
            value={stats.total.toString()}
            label="Total Executions"
            color="primary"
          />
        </Col>
        <Col md={3}>
          <StatsCard
            icon="bi bi-arrow-repeat"
            value={stats.running.toString()}
            label="Running"
            color="info"
          />
        </Col>
        <Col md={3}>
          <StatsCard
            icon="bi bi-check-circle"
            value={stats.success.toString()}
            label="Successful"
            color="success"
            trend={stats.successRate !== '0' ? { value: parseFloat(stats.successRate), direction: 'up' as const } : undefined}
          />
        </Col>
        <Col md={3}>
          <StatsCard
            icon="bi bi-x-circle"
            value={stats.failed.toString()}
            label="Failed"
            color="danger"
          />
        </Col>
      </Row>

      {/* Search Bar */}
      <Row className="mb-3">
        <Col>
          <SearchBar
            value={searchTerm}
            onChange={setSearchTerm}
            placeholder="Search by execution ID or initiator..."
          />
        </Col>
      </Row>

      {/* Filter Panel */}
      <FilterPanel
        filters={filters}
        filterConfigs={filterConfigs}
        onFilterChange={handleFilterChange}
        onClearFilters={handleClearFilters}
        collapsible
      />

      {error && <ErrorAlert error={error} onDismiss={() => setError(null)} />}

      {/* Data Table */}
      {!loading && executions.length === 0 ? (
        <EmptyState
          icon="bi bi-inbox"
          title="No Executions Found"
          message="This orchestration doesn't have any execution history yet. Start a new execution to see it here."
        />
      ) : (
        <>
          <DataTable
            data={executions}
            columns={columns}
            onRowClick={handleRowClick}
            emptyMessage="No executions found matching your criteria."
          />

          {/* Pagination */}
          <div className="mt-4">
            <PaginationControls
              currentPage={currentPage}
              totalPages={totalPages}
              pageSize={pageSize}
              totalElements={totalElements}
              onPageChange={handlePageChange}
              onPageSizeChange={handlePageSizeChange}
            />
          </div>
        </>
      )}
    </Container>
  );
};

export default OrchestrationExecutions;
