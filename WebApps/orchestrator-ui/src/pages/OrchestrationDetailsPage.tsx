import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Card, Row, Col, ProgressBar, Button } from 'react-bootstrap';
import { fetchOrchestrationDetails } from '@api/orchestrationDetailsApi';
import {
  PageHeader,
  LoadingSpinner,
  ErrorAlert,
  StatusBadge,
  EmptyState,
} from '@components';
import type { OrchestrationDetails, OrchestrationStep } from '@types';

const OrchestrationDetailsPage: React.FC = () => {
  const { orchName } = useParams<{ orchName: string }>();
  const navigate = useNavigate();
  const [details, setDetails] = useState<OrchestrationDetails | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const loadDetails = async () => {
      if (!orchName) return;
      setLoading(true);
      setError(null);
      try {
        const result = await fetchOrchestrationDetails(orchName);
        setDetails(result);
      } catch (err) {
        setError(err instanceof Error ? err.message : 'Failed to load orchestration details');
      } finally {
        setLoading(false);
      }
    };
    loadDetails();
  }, [orchName]);

  if (loading) {
    return <LoadingSpinner fullPage message="Loading orchestration details..." />;
  }

  if (error) {
    return (
      <div className="container-fluid py-4">
        <ErrorAlert error={error} />
        <Button variant="secondary" onClick={() => navigate(-1)}>
          <i className="bi bi-arrow-left me-2"></i>
          Go Back
        </Button>
      </div>
    );
  }

  if (!details) {
    return (
      <div className="container-fluid py-4">
        <EmptyState
          icon="bi bi-inbox"
          title="No orchestration found"
          description="The requested orchestration could not be found."
          action={{
            label: 'Go Back',
            onClick: () => navigate(-1),
          }}
        />
      </div>
    );
  }

  const completedSteps = details.steps.filter((s) => s.status === 'SUCCESS').length;
  const totalSteps = details.steps.length;
  const progress = totalSteps > 0 ? (completedSteps / totalSteps) * 100 : 0;

  return (
    <div className="container-fluid py-4">
      <PageHeader
        title={details.orchName}
        subtitle="Orchestration Details"
        breadcrumbs={[
          { label: 'Home', href: '/' },
          { label: 'Orchestrations', href: '/orchestrations-page' },
          { label: details.orchName },
        ]}
        actions={
          <div className="d-flex gap-2">
            <Button variant="secondary" onClick={() => navigate(-1)}>
              <i className="bi bi-arrow-left me-2"></i>
              Back
            </Button>
            <Button
              variant="primary"
              onClick={() => navigate(`/executions?orchName=${encodeURIComponent(orchName!)}`)}
            >
              <i className="bi bi-list-check me-2"></i>
              View Executions
            </Button>
          </div>
        }
      />

      <Row className="g-4 mb-4">
        <Col xs={12} md={3}>
          <Card className="shadow-sm">
            <Card.Body>
              <div className="text-muted small mb-1">Type</div>
              <div className="fw-semibold">
                <span className="badge bg-secondary">{details.type}</span>
              </div>
            </Card.Body>
          </Card>
        </Col>
        <Col xs={12} md={3}>
          <Card className="shadow-sm">
            <Card.Body>
              <div className="text-muted small mb-1">Status</div>
              <div className="fw-semibold">
                <StatusBadge status={details.status} showIcon />
              </div>
            </Card.Body>
          </Card>
        </Col>
        <Col xs={12} md={3}>
          <Card className="shadow-sm">
            <Card.Body>
              <div className="text-muted small mb-1">Initiator</div>
              <div className="fw-semibold">{details.initiator}</div>
            </Card.Body>
          </Card>
        </Col>
        <Col xs={12} md={3}>
          <Card className="shadow-sm">
            <Card.Body>
              <div className="text-muted small mb-1">Progress</div>
              <div className="fw-semibold">
                {completedSteps} / {totalSteps} Steps
              </div>
            </Card.Body>
          </Card>
        </Col>
      </Row>

      <Card className="shadow-sm">
        <Card.Header className="bg-white">
          <h5 className="mb-0">
            <i className="bi bi-list-ol me-2"></i>
            Workflow Steps
          </h5>
        </Card.Header>
        <Card.Body>
          {totalSteps > 0 && (
            <div className="mb-4">
              <div className="d-flex justify-content-between mb-2">
                <span className="small text-muted">Overall Progress</span>
                <span className="small fw-semibold">{Math.round(progress)}%</span>
              </div>
              <ProgressBar now={progress} variant={progress === 100 ? 'success' : 'primary'} />
            </div>
          )}

          {details.steps.length === 0 ? (
            <EmptyState
              icon="bi bi-diagram-2"
              title="No steps configured"
              description="This orchestration has no steps defined."
            />
          ) : (
            <div className="position-relative">
              {/* Timeline connector */}
              <div
                className="position-absolute start-0 top-0 bottom-0 bg-light"
                style={{ width: '2px', left: '1.5rem' }}
              />

              {details.steps.map((step: OrchestrationStep, index: number) => (
                <div key={step.seq} className="position-relative mb-4 pb-3">
                  <div className="d-flex align-items-start">
                    {/* Step number circle */}
                    <div
                      className={`position-relative rounded-circle d-flex align-items-center justify-content-center fw-bold text-white`}
                      style={{
                        width: '3rem',
                        height: '3rem',
                        minWidth: '3rem',
                        backgroundColor:
                          step.status === 'SUCCESS'
                            ? '#10b981'
                            : step.status === 'RUNNING'
                            ? '#3b82f6'
                            : step.status === 'FAILED'
                            ? '#ef4444'
                            : '#6b7280',
                        zIndex: 1,
                      }}
                    >
                      {step.status === 'SUCCESS' ? (
                        <i className="bi bi-check-lg"></i>
                      ) : step.status === 'FAILED' ? (
                        <i className="bi bi-x-lg"></i>
                      ) : step.status === 'RUNNING' ? (
                        <i className="bi bi-arrow-right"></i>
                      ) : (
                        step.seq
                      )}
                    </div>

                    {/* Step content */}
                    <Card
                      className={`flex-grow-1 ms-3 shadow-sm ${
                        step.status === 'RUNNING' ? 'border-primary' : ''
                      }`}
                      style={{ borderWidth: step.status === 'RUNNING' ? '2px' : '1px' }}
                    >
                      <Card.Body>
                        <div className="d-flex justify-content-between align-items-start mb-2">
                          <div>
                            <h6 className="mb-1">
                              Step {step.seq}: {step.name}
                            </h6>
                            <div className="text-muted small">
                              <i className="bi bi-box me-1"></i>
                              {step.objectType}
                            </div>
                          </div>
                          <StatusBadge status={step.status} showIcon />
                        </div>
                        <div className="small text-muted">
                          <i className="bi bi-person me-1"></i>
                          Registered by: <span className="fw-semibold">{step.registeredBy}</span>
                        </div>
                      </Card.Body>
                    </Card>
                  </div>
                </div>
              ))}
            </div>
          )}
        </Card.Body>
      </Card>
    </div>
  );
};

export default OrchestrationDetailsPage;

