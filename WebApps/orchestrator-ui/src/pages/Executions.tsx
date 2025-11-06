import React, { useState, useEffect } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
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
  StatsCard
} from '@components';
import { usePagination, useDebounce } from '@hooks';
import { fetchExecutions } from '@api';
import type { Execution } from '@types';
import { formatDate, formatDuration } from '@utils';
import dayjs from 'dayjs';

interface ExecutionWithDetails extends Execution {
  executionId?: string;
  initiator?: string;
  executedSteps?: number;
  failedSteps?: number;
}

const Executions: React.FC = () => {
  const navigate = useNavigate();
  const [searchParams, setSearchParams] = useSearchParams();
  const orchNameFromUrl = searchParams.get('orchName') || '';

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [executions, setExecutions] = useState<ExecutionWithDetails[]>([]);
  const [totalElements, setTotalElements] = useState(0);
  const [searchTerm, setSearchTerm] = useState('');
  const [filters, setFilters] = useState({
    status: '',
    orchName: orchNameFromUrl,
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
    // If there's an orchName filter, fetch executions for that orchestration
    if (filters.orchName) {
      setLoading(true);
      setError(null);
      try {
        const params = {
          page: currentPage,
          size: pageSize,
          sortBy: 'startedAt',
          direction: 'desc' as const,
          status: filters.status,
          fromDate: filters.fromDate,
          toDate: filters.toDate,
        };

        const result = await fetchExecutions(filters.orchName, params);
        setExecutions(result.content as any);
        setTotalElements(result.totalElements);
        setTotalPages(result.totalPages);
      } catch (err: any) {
        setError(err.message || 'Failed to load executions');
        setExecutions([]);
        setTotalElements(0);
        setTotalPages(0);
      } finally {
        setLoading(false);
      }
    } else {
      // No orchestration selected
      setExecutions([]);
      setTotalElements(0);
      setTotalPages(0);
    }
  };

  useEffect(() => {
    loadExecutions();
  }, [currentPage, pageSize, debouncedSearch, filters]);

  useEffect(() => {
    // Update filters when URL changes
    const orchName = searchParams.get('orchName') || '';
    if (orchName !== filters.orchName) {
      setFilters(prev => ({ ...prev, orchName }));
      resetPagination();
    }
  }, [searchParams]);

  const handleFilterChange = (name: string, value: string) => {
    setFilters(prev => ({ ...prev, [name]: value }));

    // Update URL if orchName changes
    if (name === 'orchName') {
      if (value) {
        setSearchParams({ orchName: value });
      } else {
        setSearchParams({});
      }
    }

    resetPagination();
  };

  const handleClearFilters = () => {
    setFilters({
      status: '',
      orchName: '',
      fromDate: '',
      toDate: '',
    });
    setSearchTerm('');
    setSearchParams({});
    resetPagination();
  };

  const handleRowClick = (execution: ExecutionWithDetails) => {
    const execId = execution.executionId || execution.id;
    const orchName = execution.orchName || filters.orchName;
    // Stay on executions page when viewing details, but pass orchName via state
    navigate(`/executions/${execId}`, { state: { orchName } });
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
        { value: 'ROLLED_BACK', label: 'Rolled Back' },
        { value: 'CANCELLED', label: 'Cancelled' },
      ],
    },
    {
      name: 'orchName',
      label: 'Orchestration',
      type: 'text' as const,
      placeholder: 'Filter by orchestration name...',
    },
    {
      name: 'fromDate',
      label: 'From Date',
      type: 'date' as const,
    },
    {
      name: 'toDate',
      label: 'To Date',
      type: 'date' as const,
    },
  ];

  // Table columns
  const columns = [
    {
      key: 'executionId',
      label: 'Execution ID',
      sortable: true,
      render: (item: ExecutionWithDetails) => {
        const id = item.executionId || item.id || '';
        return (
          <span className="text-primary fw-semibold">
            {id.length > 16 ? `${id.substring(0, 16)}...` : id}
          </span>
        );
      },
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
      render: (item: ExecutionWithDetails) => item.initiator || '-',
    },
    {
      key: 'startedAt',
      label: 'Started At',
      sortable: true,
      render: (item: ExecutionWithDetails) => {
        const startTime = item.startedAt;
        return startTime ? formatDate(startTime) : '-';
      },
    },
    {
      key: 'completedAt',
      label: 'Completed At',
      sortable: true,
      render: (item: ExecutionWithDetails) => {
        const endTime = item.completedAt;
        return endTime ? formatDate(endTime) : <span className="text-muted">Running...</span>;
      },
    },
    {
      key: 'duration',
      label: 'Duration',
      sortable: true,
      render: (item: ExecutionWithDetails) => {
        // If duration is provided in the API response, use it
        if (item.duration) {
          return formatDuration(item.duration);
        }
        // Otherwise calculate from startedAt and completedAt
        if (item.startedAt && item.completedAt) {
          const durationMs = dayjs(item.completedAt).diff(dayjs(item.startedAt));
          return formatDuration(durationMs);
        }
        // If still running, calculate from startedAt to now
        if (item.startedAt && !item.completedAt) {
          const durationMs = dayjs().diff(dayjs(item.startedAt));
          return <span className="text-muted">{formatDuration(durationMs)} (running)</span>;
        }
        return '-';
      },
    },
    {
      key: 'executedSteps',
      label: 'Executed Steps',
      render: (item: ExecutionWithDetails) => (
        <span className="badge bg-info">{item.executedSteps || 0}</span>
      ),
    },
    {
      key: 'failedSteps',
      label: 'Failed Steps',
      render: (item: ExecutionWithDetails) => {
        const failed = item.failedSteps || 0;
        return (
          <span className={`badge ${failed > 0 ? 'bg-danger' : 'bg-secondary'}`}>
            {failed}
          </span>
        );
      },
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
            const execId = item.executionId || item.id;
            const orchName = item.orchName || filters.orchName;
            // Stay on executions page when viewing details, but pass orchName via state
            navigate(`/executions/${execId}`, { state: { orchName } });
          }}
        >
          <i className="bi bi-eye me-1"></i>
          Details
        </Button>
      ),
    },
  ];

  const breadcrumbs = filters.orchName
    ? [
        { label: 'Home', href: '/' },
        { label: 'Orchestrations', href: '/orchestrations' },
        { label: filters.orchName, href: `/orchestrations/${filters.orchName}` },
        { label: 'Executions' },
      ]
    : [
        { label: 'Home', href: '/' },
        { label: 'Executions' },
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
        title={filters.orchName ? `Execution History - ${filters.orchName}` : 'All Executions'}
        subtitle={filters.orchName
          ? `View all execution instances for ${filters.orchName}`
          : 'View and monitor all workflow executions across orchestrations'
        }
        breadcrumbs={breadcrumbs}
        actions={
          filters.orchName ? (
            <Button
              variant="outline-secondary"
              onClick={() => {
                setFilters(prev => ({ ...prev, orchName: '' }));
                setSearchParams({});
              }}
            >
              <i className="bi bi-x-circle me-2"></i>
              Clear Orchestration Filter
            </Button>
          ) : undefined
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

      {/* Empty State */}
      {executions.length === 0 && !loading && !filters.orchName && (
        <EmptyState
          icon="bi bi-info-circle"
          title="Select an Orchestration"
          message="To view executions, please select an orchestration from the filter above or navigate to the Orchestrations page and click 'View Executions' on any orchestration."
          actionText="Go to Orchestrations"
          onAction={() => navigate('/orchestrations')}
        />
      )}

      {executions.length === 0 && !loading && filters.orchName && (
        <EmptyState
          icon="bi bi-inbox"
          title="No Executions Found"
          message={`No execution history found for ${filters.orchName}. This orchestration hasn't been executed yet or there are no executions matching your filter criteria.`}
        />
      )}

      {/* Data Table */}
      {executions.length > 0 && (
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

export default Executions;

