import React, { useState, useEffect } from 'react';
import { useParams, useNavigate, useLocation } from 'react-router-dom';
import { Container, Row, Col, Card, Badge, Button, ProgressBar, OverlayTrigger, Tooltip, Form, ListGroup } from 'react-bootstrap';
import {
  PageHeader,
  LoadingSpinner,
  ErrorAlert,
  EmptyState,
  StatusBadge,
} from '@components';
import axios from 'axios';
import { formatDate, formatDuration } from '@utils';
import dayjs from 'dayjs';

interface RetryPolicy {
  maxRetries: number;
  backoffMs: number;
}

interface ExecutionStep {
  seq: number;
  name: string;
  status: 'PENDING' | 'RUNNING' | 'SUCCESS' | 'FAILED' | 'SKIPPED' | 'ROLLED_BACK';
  operationType?: 'DO' | 'UNDO';
  startTime?: string;
  endTime?: string;
  durationMs?: number;
  retryCount?: number;
  maxRetries?: number;
  rollbackTriggered?: boolean;
  workerService?: string;
  errorMessage?: string;
  failureReason?: string;
}

interface TimelineEvent {
  timestamp: string;
  event: string;
  step?: string | null;
  status?: string | null;
  reason?: string | null;
  details?: string | null;
}

interface ExecutionDetails {
  executionId: string;
  orchName: string;
  status: 'PENDING' | 'RUNNING' | 'SUCCESS' | 'FAILED' | 'CANCELLED' | 'ROLLED_BACK';
  type?: 'SEQUENTIAL' | 'PARALLEL';
  initiator?: string;
  triggeredBy?: string;
  startedAt?: string;
  completedAt?: string;
  lastUpdatedAt?: string;
  overallDurationMs?: number;
  totalSteps?: number;
  successfulSteps?: number;
  failedSteps?: number;
  rolledBackSteps?: number;
  percentageCompleted?: number;
  retryPolicy?: RetryPolicy;
  steps: ExecutionStep[];
  timeline?: TimelineEvent[];
}

const ExecutionDetailsPage: React.FC = () => {
  const { executionId, orchName } = useParams<{ executionId: string; orchName?: string }>();
  const navigate = useNavigate();
  const location = useLocation();
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [details, setDetails] = useState<ExecutionDetails | null>(null);
  const [timeline, setTimeline] = useState<TimelineEvent[]>([]);
  const [autoRefresh, setAutoRefresh] = useState(false);

  const loadExecutionDetails = async () => {
    if (!executionId) return;

    if (!details) setLoading(true); // Only show loading on first load
    setError(null);
    try {
      const orchNameToUse = orchName || (location.state as any)?.orchName || details?.orchName;

      if (orchNameToUse) {
        // Fetch execution details
        const response = await axios.get<ExecutionDetails>(
          `/api/orchestrations/${orchNameToUse}/executions/${executionId}`
        );
        setDetails(response.data);

        // Fetch timeline separately from new API
        try {
          const timelineResponse = await axios.get<TimelineEvent[]>(
            `/api/orchestrations/${orchNameToUse}/executions/${executionId}/timeline`
          );
          setTimeline(timelineResponse.data || []);
        } catch (timelineErr) {
          console.warn('Failed to load timeline, using fallback:', timelineErr);
          // Fallback to timeline from execution details if available
          setTimeline(response.data.timeline || []);
        }
      } else {
        throw new Error('Orchestration name is required to fetch execution details');
      }
    } catch (err: any) {
      setError(err.message || 'Failed to load execution details');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadExecutionDetails();
  }, [executionId, orchName, location.state]);

  // Auto-refresh logic
  useEffect(() => {
    if (!autoRefresh || !details) return;

    const isCompleted = ['SUCCESS', 'FAILED', 'ROLLED_BACK', 'CANCELLED'].includes(details.status);
    if (isCompleted) {
      setAutoRefresh(false);
      return;
    }

    const interval = setInterval(() => {
      loadExecutionDetails();
    }, 10000); // 10 seconds

    return () => clearInterval(interval);
  }, [autoRefresh, details?.status]);

  const getStatusIcon = (status: string) => {
    switch (status) {
      case 'SUCCESS': return '✅';
      case 'FAILED': return '❌';
      case 'ROLLED_BACK': return '🔁';
      case 'RUNNING': return '⏳';
      case 'PENDING': return '⏸️';
      default: return '⚪';
    }
  };

  const getEventIcon = (event: string) => {
    if (event.includes('ORCHESTRATION_STARTED') || event === 'STARTED') return '🟢';
    if (event.includes('STEP_STARTED')) return '🔵';
    if (event.includes('STEP_SUCCESS')) return '✅';
    if (event.includes('STEP_FAILED') || event.includes('FAILED')) return '🟡';
    if (event.includes('ROLLBACK_STARTED')) return '🟠';
    if (event.includes('ROLLBACK_COMPLETED')) return '🔴';
    if (event.includes('UNDO_STARTED')) return '🔄';
    if (event.includes('UNDO_COMPLETED')) return '♻️';
    if (event.includes('ORCHESTRATION_COMPLETED')) return '🟣';
    if (event.includes('COMPLETED')) return '🟢';
    return '⚪';
  };

  const getEventBadgeVariant = (event: string): string => {
    if (event.includes('ORCHESTRATION_STARTED')) return 'success';
    if (event.includes('STEP_STARTED')) return 'primary';
    if (event.includes('STEP_SUCCESS')) return 'success';
    if (event.includes('STEP_FAILED') || event.includes('FAILED')) return 'danger';
    if (event.includes('ROLLBACK_STARTED')) return 'warning';
    if (event.includes('ROLLBACK_COMPLETED')) return 'danger';
    if (event.includes('UNDO_STARTED')) return 'info';
    if (event.includes('UNDO_COMPLETED')) return 'warning';
    if (event.includes('ORCHESTRATION_COMPLETED')) return 'dark';
    if (event.includes('COMPLETED')) return 'success';
    return 'secondary';
  };

  const parseEventDetails = (details: string | null): any => {
    if (!details) return null;
    try {
      return JSON.parse(details);
    } catch {
      return { message: details };
    }
  };

  if (loading && !details) {
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

  const isCompleted = ['SUCCESS', 'FAILED', 'ROLLED_BACK', 'CANCELLED'].includes(details.status);
  const fromExecutionsPage = !orchName;

  const successSteps = details.successfulSteps ?? details.steps.filter(s => s.status === 'SUCCESS').length;
  const failedStepsCount = details.failedSteps ?? details.steps.filter(s => s.status === 'FAILED').length;
  const rolledBackStepsCount = details.rolledBackSteps ?? details.steps.filter(s => s.status === 'ROLLED_BACK').length;
  const totalSteps = details.totalSteps ?? details.steps.length;
  const percentComplete = details.percentageCompleted ?? (totalSteps > 0 ? (successSteps / totalSteps) * 100 : 0);

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
    <Container fluid className="py-4 execution-details-page">
      <style>
        {`
          .execution-details-page .sticky-header {
            position: sticky;
            top: 0;
            z-index: 100;
            background: white;
            padding-top: 1rem;
            padding-bottom: 1rem;
          }
          .step-card-flow {
            display: flex;
            gap: 1rem;
            overflow-x: auto;
            padding: 1rem 0;
          }
          .step-card-item {
            min-width: 280px;
            flex-shrink: 0;
          }
          .step-arrow {
            display: flex;
            align-items: center;
            font-size: 2rem;
            color: #6c757d;
            flex-shrink: 0;
          }
          .fade-in {
            animation: fadeIn 0.5s ease-in;
          }
          @keyframes fadeIn {
            from { opacity: 0; transform: translateY(10px); }
            to { opacity: 1; transform: translateY(0); }
          }
          .retry-progress {
            height: 4px;
            background: #e9ecef;
            border-radius: 2px;
            overflow: hidden;
          }
          .retry-progress-bar {
            height: 100%;
            background: linear-gradient(90deg, #28a745, #ffc107);
            transition: width 0.3s;
          }
          .timeline-event {
            transition: background-color 0.2s;
          }
          .timeline-event:hover {
            background-color: #f8f9fa;
          }
          .status-legend {
            display: flex;
            gap: 1rem;
            flex-wrap: wrap;
          }
          .status-legend-item {
            display: flex;
            align-items: center;
            gap: 0.5rem;
            font-size: 0.875rem;
          }
        `}
      </style>

      {/* Sticky Header */}
      <div className="sticky-header">
        <PageHeader
          title={
            <span>
              {getStatusIcon(details.status)} Execution Details
            </span>
          }
          subtitle={`${details.orchName} - ${details.executionId.substring(0, 16)}...`}
          breadcrumbs={breadcrumbs}
          actions={
            <div className="d-flex gap-2 align-items-center">
              {!isCompleted && (
                <Form.Check
                  type="switch"
                  id="auto-refresh-switch"
                  label={`Auto-refresh ${autoRefresh ? '🔄' : ''}`}
                  checked={autoRefresh}
                  onChange={(e) => setAutoRefresh(e.target.checked)}
                  className="me-2"
                />
              )}
              <Button
                variant="secondary"
                size="sm"
                onClick={() => fromExecutionsPage
                  ? navigate('/executions')
                  : navigate(`/executions?orchName=${details.orchName}`)
                }
              >
                <i className="bi bi-arrow-left me-1"></i>
                Back
              </Button>
            </div>
          }
        />
      </div>

      {/* 1️⃣ HEADER SECTION - Summary Card */}
      <Card className="mb-4 shadow-sm border-0 fade-in">
        <Card.Body className="p-4">
          <Row className="align-items-center mb-3">
            <Col>
              <h4 className="mb-0">
                <i className="bi bi-diagram-3 me-2"></i>
                {details.orchName}
              </h4>
            </Col>
            <Col xs="auto">
              <div className="d-flex gap-2 align-items-center">
                <span className="fs-2">{getStatusIcon(details.status)}</span>
                <StatusBadge status={details.status} showIcon />
              </div>
            </Col>
          </Row>

          <Row className="g-3">
            <Col md={3}>
              <small className="text-muted d-block">Execution ID</small>
              <OverlayTrigger
                placement="top"
                overlay={<Tooltip>{details.executionId}</Tooltip>}
              >
                <code className="d-block text-truncate">{details.executionId}</code>
              </OverlayTrigger>
            </Col>
            <Col md={3}>
              <small className="text-muted d-block">Type</small>
              <Badge bg="info" className="mt-1">
                <i className={`bi ${details.type === 'SEQUENTIAL' ? 'bi-arrow-right-circle' : 'bi-arrows-angle-expand'} me-1`}></i>
                {details.type || 'SEQUENTIAL'}
              </Badge>
            </Col>
            <Col md={3}>
              <small className="text-muted d-block">Initiator</small>
              <span className="fw-semibold">{details.initiator || '-'}</span>
            </Col>
            <Col md={3}>
              <small className="text-muted d-block">Triggered By</small>
              <Badge bg="secondary">{details.triggeredBy || 'SYSTEM'}</Badge>
            </Col>
          </Row>

          <hr className="my-3" />

          <Row className="g-3">
            <Col md={3}>
              <small className="text-muted d-block">Started At</small>
              <span>{details.startedAt ? formatDate(details.startedAt) : '-'}</span>
            </Col>
            <Col md={3}>
              <small className="text-muted d-block">Completed At</small>
              <span>{details.completedAt ? formatDate(details.completedAt) : <Badge bg="info">Running...</Badge>}</span>
            </Col>
            <Col md={3}>
              <small className="text-muted d-block">Duration</small>
              <span className="fw-bold text-primary">
                {formatDuration(details.overallDurationMs)}
              </span>
            </Col>
            <Col md={3}>
              <small className="text-muted d-block">Progress</small>
              <div className="d-flex align-items-center gap-2">
                <ProgressBar
                  now={percentComplete}
                  variant={details.status === 'SUCCESS' ? 'success' : details.status === 'FAILED' ? 'danger' : 'info'}
                  style={{ flex: 1, height: '8px' }}
                />
                <span className="small fw-semibold">{percentComplete.toFixed(0)}%</span>
              </div>
            </Col>
          </Row>

          {details.retryPolicy && (
            <>
              <hr className="my-3" />
              <Row>
                <Col md={6}>
                  <small className="text-muted d-block">Retry Policy</small>
                  <span>
                    <i className="bi bi-arrow-repeat me-1"></i>
                    Max Retries: <strong>{details.retryPolicy.maxRetries}</strong> |
                    Backoff: <strong>{formatDuration(details.retryPolicy.backoffMs)}</strong>
                  </span>
                </Col>
                <Col md={6}>
                  <small className="text-muted d-block">Steps Summary</small>
                  <div className="d-flex gap-2">
                    <Badge bg="success">{successSteps} Success</Badge>
                    <Badge bg="danger">{failedStepsCount} Failed</Badge>
                    <Badge bg="warning">{rolledBackStepsCount} Rolled Back</Badge>
                    <Badge bg="secondary">{totalSteps} Total</Badge>
                  </div>
                </Col>
              </Row>
            </>
          )}
        </Card.Body>
      </Card>

      {/* Status Legend */}
      <Card className="mb-4 shadow-sm border-0 fade-in">
        <Card.Body>
          <h6 className="mb-3">
            <i className="bi bi-palette me-2"></i>
            Status Legend
          </h6>
          <div className="status-legend">
            <div className="status-legend-item">
              <StatusBadge status="SUCCESS" showIcon />
              <span>Success</span>
            </div>
            <div className="status-legend-item">
              <StatusBadge status="FAILED" showIcon />
              <span>Failed</span>
            </div>
            <div className="status-legend-item">
              <StatusBadge status="ROLLED_BACK" showIcon />
              <span>Rolled Back</span>
            </div>
            <div className="status-legend-item">
              <StatusBadge status="RUNNING" showIcon />
              <span>Running</span>
            </div>
            <div className="status-legend-item">
              <StatusBadge status="PENDING" showIcon />
              <span>Pending</span>
            </div>
          </div>
        </Card.Body>
      </Card>

      <Row className="g-4">
        <Col lg={8}>
          {/* 2️⃣ STEP FLOW VISUALIZATION */}
          <Card className="mb-4 shadow-sm border-0 fade-in">
            <Card.Body>
              <h5 className="mb-3">
                <i className="bi bi-diagram-2 me-2"></i>
                Step Flow {details.type === 'PARALLEL' && <Badge bg="info" className="ms-2">Parallel</Badge>}
              </h5>

              <div className="step-card-flow">
                {details.steps.map((step, index) => (
                  <React.Fragment key={step.seq}>
                    <div className="step-card-item">
                      <Card className={`h-100 ${step.status === 'RUNNING' ? 'border-primary border-2' : ''}`}>
                        <Card.Header className="d-flex justify-content-between align-items-center py-2">
                          <div className="d-flex align-items-center gap-2">
                            <Badge bg="dark" pill>{step.seq}</Badge>
                            <small className="fw-semibold">{step.name}</small>
                            {step.rollbackTriggered && <span title="Rollback triggered">🔁</span>}
                          </div>
                          <StatusBadge status={step.status} />
                        </Card.Header>
                        <Card.Body className="py-2">
                          <div className="mb-2">
                            <small className="text-muted">Operation</small>
                            <div>
                              <Badge bg={step.operationType === 'DO' ? 'primary' : 'warning'} className="me-1">
                                {step.operationType || 'DO'}
                              </Badge>
                            </div>
                          </div>

                          <div className="mb-2">
                            <small className="text-muted">Duration</small>
                            <div className="fw-semibold">{formatDuration(step.durationMs)}</div>
                          </div>

                          {(step.retryCount !== undefined && step.maxRetries !== undefined) && (
                            <div className="mb-2">
                              <small className="text-muted">Retries: {step.retryCount}/{step.maxRetries}</small>
                              <div className="retry-progress mt-1">
                                <div
                                  className="retry-progress-bar"
                                  style={{ width: `${(step.retryCount / step.maxRetries) * 100}%` }}
                                ></div>
                              </div>
                            </div>
                          )}

                          {step.workerService && (
                            <div>
                              <small className="text-muted">Worker</small>
                              <div className="small text-truncate">{step.workerService}</div>
                            </div>
                          )}

                          {(step.failureReason || step.errorMessage) && (
                            <OverlayTrigger
                              placement="top"
                              overlay={<Tooltip>{step.failureReason || step.errorMessage}</Tooltip>}
                            >
                              <div className="mt-2">
                                <Badge bg="danger" className="w-100 text-truncate">
                                  <i className="bi bi-exclamation-triangle me-1"></i>
                                  Error
                                </Badge>
                              </div>
                            </OverlayTrigger>
                          )}
                        </Card.Body>
                      </Card>
                    </div>
                    {index < details.steps.length - 1 && details.type !== 'PARALLEL' && (
                      <div className="step-arrow">→</div>
                    )}
                  </React.Fragment>
                ))}
              </div>
            </Card.Body>
          </Card>
        </Col>

        <Col lg={4}>
          {/* 3️⃣ TIMELINE SECTION */}
          {timeline && timeline.length > 0 && (
            <Card className="shadow-sm border-0 fade-in">
              <Card.Body>
                <h5 className="mb-3">
                  <i className="bi bi-clock-history me-2"></i>
                  Timeline ({timeline.length} events)
                </h5>
                <ListGroup variant="flush" style={{ maxHeight: '600px', overflowY: 'auto' }}>
                  {timeline.map((event, index) => {
                    const parsedDetails = parseEventDetails(event.details);
                    return (
                      <ListGroup.Item key={index} className="timeline-event px-0 border-start-0 border-end-0">
                        <div className="d-flex align-items-start gap-2">
                          <span className="fs-5">{getEventIcon(event.event)}</span>
                          <div className="flex-grow-1">
                            <div className="d-flex justify-content-between align-items-start mb-1">
                              <Badge bg={getEventBadgeVariant(event.event)} className="text-wrap text-start">
                                {event.event.replace(/_/g, ' ')}
                              </Badge>
                              <small className="text-muted" style={{ whiteSpace: 'nowrap' }}>
                                {dayjs(event.timestamp).format('HH:mm:ss.SSS')}
                              </small>
                            </div>
                            {event.step && (
                              <div className="small mb-1">
                                <i className="bi bi-arrow-right-circle me-1"></i>
                                <strong>Step:</strong> <code>{event.step}</code>
                              </div>
                            )}
                            {event.status && (
                              <div className="small mb-1">
                                <StatusBadge status={event.status} />
                              </div>
                            )}
                            {parsedDetails && (
                              <div className="small text-muted mt-1">
                                {parsedDetails.message && (
                                  <div><i className="bi bi-info-circle me-1"></i>{parsedDetails.message}</div>
                                )}
                                {parsedDetails.initiator && (
                                  <div><strong>Initiator:</strong> {parsedDetails.initiator}</div>
                                )}
                                {parsedDetails.worker !== undefined && (
                                  <div><strong>Worker:</strong> {parsedDetails.worker || 'N/A'}</div>
                                )}
                                {parsedDetails.durationMs !== undefined && (
                                  <div><strong>Duration:</strong> {formatDuration(parsedDetails.durationMs)}</div>
                                )}
                                {parsedDetails.retryCount !== undefined && (
                                  <div><strong>Retry Count:</strong> {parsedDetails.retryCount}</div>
                                )}
                                {parsedDetails.stepsToRollback !== undefined && (
                                  <div><strong>Steps to Rollback:</strong> {parsedDetails.stepsToRollback}</div>
                                )}
                                {parsedDetails.rolledBackSteps !== undefined && (
                                  <div><strong>Rolled Back Steps:</strong> {parsedDetails.rolledBackSteps}</div>
                                )}
                              </div>
                            )}
                            {event.reason && (
                              <div className="small text-warning mt-1">
                                <i className="bi bi-exclamation-circle me-1"></i>
                                {event.reason}
                              </div>
                            )}
                          </div>
                        </div>
                      </ListGroup.Item>
                    );
                  })}
                </ListGroup>
              </Card.Body>
            </Card>
          )}
        </Col>
      </Row>
    </Container>
  );
};

export default ExecutionDetailsPage;

