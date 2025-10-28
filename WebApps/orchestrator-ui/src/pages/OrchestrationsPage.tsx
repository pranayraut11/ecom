import React, { useEffect, useState } from 'react';
import { fetchOrchestrations } from '../api/orchestrationsApi';
import { Spinner, Alert, Table, Form, Pagination, Button } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';

interface Orchestration {
  orchName: string;
  type: 'Sequential' | 'Simultaneous';
  status: string;
  initiatorName?: string;
  initiator?: string;
  registeredWorkersCount?: number;
  totalWorkersExpected?: number;
  lastUpdated?: string;
}

const OrchestrationsPage: React.FC = () => {
  const [data, setData] = useState<Orchestration[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(10);
  const [sortBy, setSortBy] = useState('orchName');
  const [direction, setDirection] = useState('asc');
  const [filters, setFilters] = useState({
    status: '',
    type: '',
    orchName: '',
    registeredFrom: '',
    registeredTo: '',
  });
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const navigate = useNavigate();

  useEffect(() => {
    const loadData = async () => {
      setLoading(true);
      setError(null);
      try {
        const orchestrations = await fetchOrchestrations({
          page,
          size,
          sortBy,
          direction,
          ...filters,
        });
        console.log('API Response:', orchestrations);
        setData(Array.isArray(orchestrations.content) ? orchestrations.content : []);
        setTotalPages(orchestrations.totalPages || 0);
        setTotalElements(orchestrations.totalElements || 0);
      } catch (err: any) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };
    loadData();
  }, [page, size, sortBy, direction, filters]);

  const handleFilterChange = (e: React.ChangeEvent<any>) => {
    const { name, value } = e.target;
    setFilters((prev) => ({ ...prev, [name]: value }));
  };

  const handleSort = (key: string) => {
    setSortBy(key);
    setDirection((prev) => (prev === 'asc' ? 'desc' : 'asc'));
  };

  return (
    <div className="container mt-4">
      <h1>Orchestrations</h1>
      {loading && <Spinner animation="border" />}
      {error && <Alert variant="danger">{error}</Alert>}
      <Form className="mb-3">
        <Form.Group controlId="filterOrchName">
          <Form.Label>Orchestration Name</Form.Label>
          <Form.Control
            type="text"
            name="orchName"
            value={filters.orchName}
            onChange={handleFilterChange}
          />
        </Form.Group>
        <Form.Group controlId="filterType" className="mt-3">
          <Form.Label>Type</Form.Label>
          <Form.Select name="type" value={filters.type} onChange={handleFilterChange}>
            <option value="">All</option>
            <option value="Sequential">Sequential</option>
            <option value="Simultaneous">Simultaneous</option>
          </Form.Select>
        </Form.Group>
        <Form.Group controlId="filterStatus" className="mt-3">
          <Form.Label>Status</Form.Label>
          <Form.Select name="status" value={filters.status} onChange={handleFilterChange}>
            <option value="">All</option>
            <option value="Success">Success</option>
            <option value="Partial">Partial</option>
            <option value="Failed">Failed</option>
          </Form.Select>
        </Form.Group>
        <Form.Group controlId="filterRegisteredFrom" className="mt-3">
          <Form.Label>Registered From</Form.Label>
          <Form.Control
            type="date"
            name="registeredFrom"
            value={filters.registeredFrom}
            onChange={handleFilterChange}
          />
        </Form.Group>
        <Form.Group controlId="filterRegisteredTo" className="mt-3">
          <Form.Label>Registered To</Form.Label>
          <Form.Control
            type="date"
            name="registeredTo"
            value={filters.registeredTo}
            onChange={handleFilterChange}
          />
        </Form.Group>
      </Form>
      <Table striped bordered hover>
        <thead>
          <tr>
            <th onClick={() => handleSort('orchName')}>Name</th>
            <th>Type</th>
            <th>Initiator</th>
            <th>Registered Workers</th>
            <th>Total Workers</th>
            <th>Last Updated</th>
            <th onClick={() => handleSort('status')}>Status</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {data.map((item, index) => (
            <tr key={index}>
              <td>{item.orchName}</td>
              <td>{item.type}</td>
              <td>{item.initiatorName || item.initiator}</td>
              <td>{item.registeredWorkersCount}</td>
              <td>{item.totalWorkersExpected}</td>
              <td>{item.lastUpdated ? new Date(item.lastUpdated).toLocaleString() : '-'}</td>
              <td>{item.status}</td>
              <td>
                <Button
                  variant="primary"
                  size="sm"
                  onClick={() => navigate(`/orchestrations/${item.orchName}`)}
                  className="me-2"
                >
                  Details
                </Button>
                <Button
                  variant="info"
                  size="sm"
                  onClick={() => navigate(`/orchestrations/${item.orchName}/executions`)}
                >
                  View Executions
                </Button>
              </td>
            </tr>
          ))}
        </tbody>
      </Table>
      <Pagination>
        {Array.from({ length: totalPages }, (_, i) => (
          <Pagination.Item
            key={i}
            active={i === page}
            onClick={() => setPage(i)}
          >
            {i + 1}
          </Pagination.Item>
        ))}
      </Pagination>
    </div>
  );
};

export default OrchestrationsPage;