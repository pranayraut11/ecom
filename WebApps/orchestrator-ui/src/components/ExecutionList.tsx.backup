import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Card, Form, Table, Badge, Spinner, Alert, Pagination } from 'react-bootstrap';
import { fetchExecutions } from '../api/executionsApi';
import dayjs from 'dayjs';

interface Execution {
  executionId: string;
  status: 'SUCCESS' | 'FAILED' | 'RUNNING';
  startTime: string;
  endTime: string;
  initiator: string;
  executedSteps: number;
  failedSteps: number;
}

interface ExecutionListProps {
  orchName: string;
}

const ExecutionList: React.FC<ExecutionListProps> = ({ orchName }) => {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [executions, setExecutions] = useState<Execution[]>([]);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  
  const [filters, setFilters] = useState({
    page: 0,
    size: 10,
    sortBy: 'startTime',
    direction: 'desc',
    status: '',
    fromDate: '',
    toDate: '',
  });

  const loadExecutions = async () => {
    setLoading(true);
    setError(null);
    try {
      const result = await fetchExecutions(orchName, filters);
      setExecutions(result.content);
      setTotalPages(result.totalPages);
      setTotalElements(result.totalElements);
    } catch (err: any) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadExecutions();
  }, [filters, orchName]);

  const handleFilterChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setFilters(prev => ({ ...prev, [name]: value, page: 0 }));
  };

  const handlePageChange = (page: number) => {
    setFilters(prev => ({ ...prev, page }));
  };

  const getStatusBadgeVariant = (status: string) => {
    switch (status) {
      case 'SUCCESS':
        return 'success';
      case 'FAILED':
        return 'danger';
      case 'RUNNING':
        return 'primary';
      default:
        return 'secondary';
    }
  };

  const successRate = executions.length
    ? (executions.filter(e => e.status === 'SUCCESS').length / executions.length * 100).toFixed(1)
    : 0;

  return (
    <Card>
      <Card.Header className="bg-primary text-white">
        <h5 className="mb-0">Execution History - {orchName}</h5>
      </Card.Header>
      <Card.Body>
        {/* Summary Stats */}
        <div className="mb-3">
          <strong>Total Executions:</strong> {totalElements} |{' '}
          <strong>Success Rate:</strong> {successRate}%
        </div>

        {/* Filters */}
        <Form className="row g-3 mb-4">
          <div className="col-md-3">
            <Form.Group>
              <Form.Label>Status</Form.Label>
              <Form.Select
                name="status"
                value={filters.status}
                onChange={handleFilterChange}
              >
                <option value="">ALL</option>
                <option value="SUCCESS">SUCCESS</option>
                <option value="FAILED">FAILED</option>
                <option value="RUNNING">RUNNING</option>
              </Form.Select>
            </Form.Group>
          </div>
          <div className="col-md-3">
            <Form.Group>
              <Form.Label>From Date</Form.Label>
              <Form.Control
                type="datetime-local"
                name="fromDate"
                value={filters.fromDate}
                onChange={handleFilterChange}
              />
            </Form.Group>
          </div>
          <div className="col-md-3">
            <Form.Group>
              <Form.Label>To Date</Form.Label>
              <Form.Control
                type="datetime-local"
                name="toDate"
                value={filters.toDate}
                onChange={handleFilterChange}
              />
            </Form.Group>
          </div>
          <div className="col-md-3">
            <Form.Group>
              <Form.Label>Sort By</Form.Label>
              <Form.Select
                name="sortBy"
                value={filters.sortBy}
                onChange={handleFilterChange}
              >
                <option value="startTime">Start Time</option>
                <option value="endTime">End Time</option>
              </Form.Select>
            </Form.Group>
          </div>
        </Form>

        {/* Loading and Error States */}
        {loading && (
          <div className="text-center my-4">
            <Spinner animation="border" role="status">
              <span className="visually-hidden">Loading...</span>
            </Spinner>
          </div>
        )}
        {error && <Alert variant="danger">{error}</Alert>}

        {/* Executions Table */}
        <div className="table-responsive">
          <Table striped bordered hover>
            <thead>
              <tr>
                <th>Execution ID</th>
                <th>Status</th>
                <th>Initiator</th>
                <th>Start Time</th>
                <th>End Time</th>
                <th>Executed Steps</th>
                <th>Failed Steps</th>
              </tr>
            </thead>
            <tbody>
              {executions.map((execution) => (
                <tr
                  key={execution.executionId}
                  onClick={() => navigate(`/executions/${execution.executionId}`)}
                  style={{ cursor: 'pointer' }}
                >
                  <td>{execution.executionId}</td>
                  <td>
                    <Badge bg={getStatusBadgeVariant(execution.status)}>
                      {execution.status}
                    </Badge>
                  </td>
                  <td>{execution.initiator}</td>
                  <td>{dayjs(execution.startTime).format('YYYY-MM-DD HH:mm:ss')}</td>
                  <td>{execution.endTime ? dayjs(execution.endTime).format('YYYY-MM-DD HH:mm:ss') : '-'}</td>
                  <td>{execution.executedSteps}</td>
                  <td>{execution.failedSteps}</td>
                </tr>
              ))}
            </tbody>
          </Table>
        </div>

        {/* Pagination */}
        <div className="d-flex justify-content-between align-items-center">
          <div>
            Showing {executions.length} of {totalElements} executions
          </div>
          <Pagination>
            <Pagination.First
              onClick={() => handlePageChange(0)}
              disabled={filters.page === 0}
            />
            <Pagination.Prev
              onClick={() => handlePageChange(filters.page - 1)}
              disabled={filters.page === 0}
            />
            {Array.from({ length: totalPages }, (_, i) => (
              <Pagination.Item
                key={i}
                active={i === filters.page}
                onClick={() => handlePageChange(i)}
              >
                {i + 1}
              </Pagination.Item>
            ))}
            <Pagination.Next
              onClick={() => handlePageChange(filters.page + 1)}
              disabled={filters.page === totalPages - 1}
            />
            <Pagination.Last
              onClick={() => handlePageChange(totalPages - 1)}
              disabled={filters.page === totalPages - 1}
            />
          </Pagination>
        </div>
      </Card.Body>
    </Card>
  );
};

export default ExecutionList;