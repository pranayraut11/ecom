import React, { useState, useEffect } from 'react';
import { useParams, useNavigate, useLocation } from 'react-router-dom';
import { Container, Row, Col, Card, Badge, Button, ProgressBar } from 'react-bootstrap';
import {
  PageHeader,
  LoadingSpinner,
  ErrorAlert,
  EmptyState,
  StatusBadge,
} from '@components';
import axios from 'axios';
import { formatDate } from '@utils';

interface ExecutionStep {
  seq: number;
  name: string;
  status: 'PENDING' | 'RUNNING' | 'SUCCESS' | 'FAILED' | 'SKIPPED';
  startTime?: string;
  endTime?: string;
  durationMs?: number;
  workerService?: string;
  errorMessage?: string;
}

interface ExecutionDetails {
  executionId: string;
  orchName: string;
  status: 'PENDING' | 'RUNNING' | 'SUCCESS' | 'FAILED' | 'CANCELLED';
  startTime?: string;
  endTime?: string;
  initiator?: string;
  steps: ExecutionStep[];
}

const ExecutionDetailsPage: React.FC = () => {
  const { executionId, orchName } = useParams<{ executionId: string; orchName?: string }>();
  const navigate = useNavigate();
  const location = useLocation();
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [details, setDetails] = useState<ExecutionDetails | null>(null);

  useEffect(() => {
    const loadExecutionDetails = async () => {
      if (!executionId) return;

      setLoading(true);
      setError(null);
      try {
        // Get orchName from URL params, location state, or already loaded details
        const orchNameToUse = orchName || (location.state as any)?.orchName || details?.orchName;

        if (orchNameToUse) {
          const response = await axios.get<ExecutionDetails>(
            `/api/orchestrations/${orchNameToUse}/executions/${executionId}`
          );
          setDetails(response.data);
        } else {
          // If no orchName available, you might need a different endpoint
          throw new Error('Orchestration name is required to fetch execution details');
        }
      } catch (err: any) {
        setError(err.message || 'Failed to load execution details');
      } finally {
        setLoading(false);
      }
    };

    loadExecutionDetails();
  }, [executionId, orchName, location.state]);

  if (loading) {
    return <LoadingSpinner message="Loading execution details..." />;
  }

  if (error || !details) {
    return (
      <Container fluid className="py-4">
        <ErrorAlert error={error || 'Execution not found'} />
        <EmptyState
          icon="bi bi-exclamation-triangle"
          title="Unable to Load Execution"
          message="The execution details could not be loaded. Please try again."
          actionText="Go Back"
          onAction={() => navigate(-1)}
        />
      </Container>
    );
  }

  const completedSteps = details.steps.filter(s => s.status === 'SUCCESS').length;
  const failedSteps = details.steps.filter(s => s.status === 'FAILED').length;
  const totalSteps = details.steps.length;
  const progressPercentage = totalSteps > 0 ? (completedSteps / totalSteps) * 100 : 0;

  const calculateDuration = (startTime?: string, endTime?: string) => {
    if (!startTime || !endTime) return null;
    const start = new Date(startTime).getTime();
    const end = new Date(endTime).getTime();
    return end - start;
  };

  const formatDuration = (ms?: number) => {
    if (!ms) return '-';
    const seconds = Math.floor(ms / 1000);
    const minutes = Math.floor(seconds / 60);
    const hours = Math.floor(minutes / 60);
    if (hours > 0) return `${hours}h ${minutes % 60}m ${seconds % 60}s`;
    if (minutes > 0) return `${minutes}m ${seconds % 60}s`;
    return `${seconds}s`;
  };

  const totalDuration = calculateDuration(details.startTime, details.endTime);

  const getStepStatusIcon = (status: string) => {
    switch (status) {
      case 'SUCCESS':
        return 'bi bi-check-circle-fill text-success';
      case 'FAILED':
        return 'bi bi-x-circle-fill text-danger';
      case 'RUNNING':
        return 'bi bi-arrow-repeat text-primary';
      case 'PENDING':
        return 'bi bi-clock text-secondary';
      case 'SKIPPED':
        return 'bi bi-dash-circle text-muted';
      default:
        return 'bi bi-circle text-secondary';
    }
  };

  const getStepStatusColor = (status: string) => {
    switch (status) {
      case 'SUCCESS':
        return '#28a745';
      case 'FAILED':
        return '#dc3545';
      case 'RUNNING':
        return '#007bff';
      case 'PENDING':
        return '#6c757d';
      case 'SKIPPED':
        return '#adb5bd';
      default:
        return '#6c757d';
    }
  };

  // Determine if we came from the executions page or orchestrations page
  const fromExecutionsPage = !orchName; // If orchName is not in URL params, we came from /executions

  const breadcrumbs = fromExecutionsPage
    ? [
        { label: 'Home', href: '/' },
        { label: 'Executions', href: '/executions' },
        { label: executionId?.substring(0, 8) || 'Details' },
      ]
    : [
        { label: 'Home', href: '/' },
        { label: 'Orchestrations', href: '/orchestrations' },
        ...(details.orchName ? [{ label: details.orchName, href: `/orchestrations/${details.orchName}` }] : []),
        { label: 'Executions', href: `/executions?orchName=${details.orchName}` },
        { label: executionId?.substring(0, 8) || 'Details' },
      ];

  return (
    <Container fluid className="py-4">
      <PageHeader
        title={`Execution Details`}
        subtitle={`${details.orchName} - ${details.executionId}`}
        breadcrumbs={breadcrumbs}
        actions={
          <div className="d-flex gap-2">
            <Button
              variant="secondary"
              onClick={() => fromExecutionsPage
                ? navigate('/executions')
                : navigate(`/executions?orchName=${details.orchName}`)
              }
            >
              <i className="bi bi-arrow-left me-2"></i>
              Back to Executions
            </Button>
            <Button variant="outline-primary" onClick={() => navigate(`/orchestrations/${details.orchName}`)}>
              <i className="bi bi-diagram-3 me-2"></i>
              View Orchestration
            </Button>
          </div>
        }
      />

      {/* Summary Cards */}
      <Row className="g-4 mb-4">
        <Col md={3}>
          <Card className="h-100 shadow-sm">
            <Card.Body>
              <div className="d-flex align-items-center mb-2">
                <i className="bi bi-info-circle me-2 text-primary" style={{ fontSize: '1.5rem' }}></i>
                <h6 className="mb-0 text-muted">Execution ID</h6>
              </div>
              <p className="h6 mb-0 text-truncate" title={details.executionId}>
                {details.executionId}
              </p>
            </Card.Body>
          </Card>
        </Col>
        <Col md={3}>
          <Card className="h-100 shadow-sm">
            <Card.Body>
              <div className="d-flex align-items-center mb-2">
                <i className="bi bi-flag me-2 text-info" style={{ fontSize: '1.5rem' }}></i>
                <h6 className="mb-0 text-muted">Status</h6>
              </div>
              <StatusBadge status={details.status} showIcon />
            </Card.Body>
          </Card>
        </Col>
        <Col md={3}>
          <Card className="h-100 shadow-sm">
            <Card.Body>
              <div className="d-flex align-items-center mb-2">
                <i className="bi bi-clock-history me-2 text-warning" style={{ fontSize: '1.5rem' }}></i>
                <h6 className="mb-0 text-muted">Duration</h6>
              </div>
              <p className="h6 mb-0">{formatDuration(totalDuration || undefined)}</p>
            </Card.Body>
          </Card>
        </Col>
        <Col md={3}>
          <Card className="h-100 shadow-sm">
            <Card.Body>
              <div className="d-flex align-items-center mb-2">
                <i className="bi bi-list-check me-2 text-success" style={{ fontSize: '1.5rem' }}></i>
                <h6 className="mb-0 text-muted">Progress</h6>
              </div>
              <p className="h6 mb-0">
                {completedSteps}/{totalSteps} Steps
              </p>
            </Card.Body>
          </Card>
        </Col>
      </Row>

      {/* Execution Info */}
      <Card className="mb-4 shadow-sm">
        <Card.Body>
          <h5 className="mb-3">
            <i className="bi bi-info-circle me-2"></i>
            Execution Information
          </h5>
          <Row>
            <Col md={6}>
              <div className="mb-3">
                <small className="text-muted">Orchestration Name</small>
                <p className="mb-0 fw-semibold">{details.orchName}</p>
              </div>
              <div className="mb-3">
                <small className="text-muted">Started At</small>
                <p className="mb-0">{details.startTime ? formatDate(details.startTime) : '-'}</p>
              </div>
            </Col>
            <Col md={6}>
              <div className="mb-3">
                <small className="text-muted">Initiator</small>
                <p className="mb-0">{details.initiator || '-'}</p>
              </div>
              <div className="mb-3">
                <small className="text-muted">Completed At</small>
                <p className="mb-0">{details.endTime ? formatDate(details.endTime) : '-'}</p>
              </div>
            </Col>
          </Row>
        </Card.Body>
      </Card>

      {/* Overall Progress */}
      <Card className="mb-4 shadow-sm">
        <Card.Body>
          <div className="d-flex justify-content-between align-items-center mb-2">
            <h5 className="mb-0">
              <i className="bi bi-bar-chart me-2"></i>
              Overall Progress
            </h5>
            <span className="text-muted">
              {progressPercentage.toFixed(0)}% Complete
            </span>
          </div>
          <ProgressBar>
            <ProgressBar variant="success" now={(completedSteps / totalSteps) * 100} key={1} />
            <ProgressBar variant="danger" now={(failedSteps / totalSteps) * 100} key={2} />
          </ProgressBar>
          <div className="d-flex justify-content-between mt-2">
            <small className="text-success">
              <i className="bi bi-check-circle me-1"></i>
              {completedSteps} Completed
            </small>
            <small className="text-danger">
              <i className="bi bi-x-circle me-1"></i>
              {failedSteps} Failed
            </small>
            <small className="text-muted">
              <i className="bi bi-clock me-1"></i>
              {totalSteps - completedSteps - failedSteps} Pending
            </small>
          </div>
        </Card.Body>
      </Card>

      {/* Steps Timeline */}
      <Card className="shadow-sm">
        <Card.Body>
          <h5 className="mb-4">
            <i className="bi bi-list-ol me-2"></i>
            Execution Steps Timeline
          </h5>
          <div className="timeline-container">
            {details.steps.map((step, index) => (
              <div key={step.seq} className="timeline-item position-relative mb-4">
                <div className="d-flex">
                  {/* Timeline indicator */}
                  <div className="timeline-marker me-3 d-flex flex-column align-items-center">
                    <div
                      className="rounded-circle d-flex align-items-center justify-content-center"
                      style={{
                        width: '40px',
                        height: '40px',
                        backgroundColor: getStepStatusColor(step.status),
                        color: 'white',
                        fontWeight: 'bold',
                        fontSize: '0.9rem',
                      }}
                    >
                      {step.seq}
                    </div>
                    {index < details.steps.length - 1 && (
                      <div
                        style={{
                          width: '2px',
                          flexGrow: 1,
                          minHeight: '40px',
                          backgroundColor: '#e0e0e0',
                          marginTop: '8px',
                          marginBottom: '8px',
                        }}
                      ></div>
                    )}
                  </div>

                  {/* Step content */}
                  <div className="flex-grow-1">
                    <Card
                      className={`mb-0 ${step.status === 'RUNNING' ? 'border-primary' : ''}`}
                      style={{ borderWidth: step.status === 'RUNNING' ? '2px' : '1px' }}
                    >
                      <Card.Body>
                        <div className="d-flex justify-content-between align-items-start mb-2">
                          <div>
                            <h6 className="mb-1">
                              <i className={`${getStepStatusIcon(step.status)} me-2`}></i>
                              {step.name}
                            </h6>
                            <small className="text-muted">Step {step.seq}</small>
                          </div>
                          <StatusBadge status={step.status} showIcon />
                        </div>

                        <Row className="mt-3">
                          <Col md={3}>
                            <small className="text-muted d-block">Worker Service</small>
                            <span className="small">{step.workerService || '-'}</span>
                          </Col>
                          <Col md={3}>
                            <small className="text-muted d-block">Started At</small>
                            <span className="small">
                              {step.startTime ? formatDate(step.startTime) : '-'}
                            </span>
                          </Col>
                          <Col md={3}>
                            <small className="text-muted d-block">Completed At</small>
                            <span className="small">
                              {step.endTime ? formatDate(step.endTime) : '-'}
                            </span>
                          </Col>
                          <Col md={3}>
                            <small className="text-muted d-block">Duration</small>
                            <span className="small fw-semibold">
                              {formatDuration(step.durationMs)}
                            </span>
                          </Col>
                        </Row>

                        {step.errorMessage && (
                          <div className="mt-3">
                            <div className="alert alert-danger mb-0">
                              <i className="bi bi-exclamation-triangle me-2"></i>
                              <strong>Error:</strong> {step.errorMessage}
                            </div>
                          </div>
                        )}
                      </Card.Body>
                    </Card>
                  </div>
                </div>
              </div>
            ))}
          </div>
        </Card.Body>
      </Card>
    </Container>
  );
};

export default ExecutionDetailsPage;

