import React from 'react';
import { Row, Col, Card } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';
import { PageHeader, StatsCard } from '@components';

const Dashboard: React.FC = () => {
  const navigate = useNavigate();

  return (
    <div className="container-fluid py-4">
      <PageHeader
        title="Dashboard"
        subtitle="Overview of your orchestration system"
      />

      <Row className="g-4 mb-4">
        <Col xs={12} sm={6} lg={3}>
          <StatsCard
            icon="bi bi-diagram-3"
            value="24"
            label="Total Orchestrations"
            color="primary"
            onClick={() => navigate('/orchestrations-page')}
            trend={{ value: 12, direction: 'up' }}
          />
        </Col>
        <Col xs={12} sm={6} lg={3}>
          <StatsCard
            icon="bi bi-play-circle"
            value="156"
            label="Active Executions"
            color="success"
            onClick={() => navigate('/executions')}
          />
        </Col>
        <Col xs={12} sm={6} lg={3}>
          <StatsCard
            icon="bi bi-check-circle"
            value="1,234"
            label="Completed Today"
            color="info"
            trend={{ value: 8, direction: 'up' }}
          />
        </Col>
        <Col xs={12} sm={6} lg={3}>
          <StatsCard
            icon="bi bi-exclamation-triangle"
            value="3"
            label="Failed Tasks"
            color="danger"
            onClick={() => navigate('/self-healing')}
            trend={{ value: 5, direction: 'down' }}
          />
        </Col>
      </Row>

      <Row className="g-4">
        <Col xs={12} lg={8}>
          <Card className="shadow-sm">
            <Card.Header className="bg-white">
              <h5 className="mb-0">
                <i className="bi bi-activity me-2"></i>
                Recent Activity
              </h5>
            </Card.Header>
            <Card.Body>
              <div className="text-center text-muted py-5">
                <i className="bi bi-graph-up display-3 mb-3"></i>
                <p>Activity chart will be displayed here</p>
              </div>
            </Card.Body>
          </Card>
        </Col>
        <Col xs={12} lg={4}>
          <Card className="shadow-sm">
            <Card.Header className="bg-white">
              <h5 className="mb-0">
                <i className="bi bi-clock-history me-2"></i>
                Quick Actions
              </h5>
            </Card.Header>
            <Card.Body>
              <div className="d-grid gap-2">
                <button
                  className="btn btn-outline-primary text-start"
                  onClick={() => navigate('/orchestrations-page')}
                >
                  <i className="bi bi-diagram-3 me-2"></i>
                  View All Orchestrations
                </button>
                <button
                  className="btn btn-outline-success text-start"
                  onClick={() => navigate('/executions')}
                >
                  <i className="bi bi-list-check me-2"></i>
                  View Executions
                </button>
                <button
                  className="btn btn-outline-warning text-start"
                  onClick={() => navigate('/self-healing')}
                >
                  <i className="bi bi-tools me-2"></i>
                  Self-Healing Console
                </button>
              </div>
            </Card.Body>
          </Card>
        </Col>
      </Row>
    </div>
  );
};

export default Dashboard;

