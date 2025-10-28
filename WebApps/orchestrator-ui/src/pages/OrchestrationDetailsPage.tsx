import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { fetchOrchestrationDetails } from '../api/orchestrationDetailsApi';
import { Spinner, Alert, Button, ListGroup, Badge } from 'react-bootstrap';

interface Step {
  seq: number;
  name: string;
  objectType: string;
  registeredBy: string;
  status: 'PENDING' | 'RUNNING' | 'SUCCESS' | 'FAILED';
}

interface OrchestrationMeta {
  orchName: string;
  type: string;
  status: string;
  initiator: string;
}

const OrchestrationDetailsPage: React.FC = () => {
  const { orchName } = useParams<{ orchName: string }>();
  const navigate = useNavigate();
  const [meta, setMeta] = useState<OrchestrationMeta | null>(null);
  const [steps, setSteps] = useState<Step[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const loadDetails = async () => {
      setLoading(true);
      setError(null);
      try {
        const result = await fetchOrchestrationDetails(orchName!);
        setMeta({
          orchName: result.orchName,
          type: result.type,
          status: result.status,
          initiator: result.initiator,
        });
        setSteps(result.steps || []);
      } catch (err: any) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };
    loadDetails();
  }, [orchName]);

  // Find current running step
  const currentStepIndex = steps.findIndex((step) => step.status === 'RUNNING');

  return (
    <div className="container mt-4">
      <Button variant="secondary" onClick={() => navigate(-1)} className="mb-3">Back</Button>
      <h2>Orchestration Details</h2>
      {loading && <Spinner animation="border" />}
      {error && <Alert variant="danger">{error}</Alert>}
      {meta && (
        <div className="mb-4">
          <div><strong>Name:</strong> {meta.orchName}</div>
          <div><strong>Type:</strong> {meta.type}</div>
          <div><strong>Status:</strong> {meta.status}</div>
          <div><strong>Initiator:</strong> {meta.initiator}</div>
        </div>
      )}
      <h4>Steps</h4>
      <ListGroup>
        {steps.map((step, idx) => (
          <ListGroup.Item
            key={step.seq}
            className={step.status === 'RUNNING' ? 'bg-info text-white' : ''}
          >
            <div className="d-flex justify-content-between align-items-center">
              <div>
                <strong>Step {step.seq}:</strong> {step.name} <Badge bg="secondary">{step.objectType}</Badge>
              </div>
              <div>
                <Badge bg={step.status === 'SUCCESS' ? 'success' : step.status === 'FAILED' ? 'danger' : step.status === 'RUNNING' ? 'info' : 'warning'}>
                  {step.status}
                </Badge>
              </div>
            </div>
            <div className="mt-1">
              <small><strong>Registered By:</strong> {step.registeredBy}</small>
            </div>
          </ListGroup.Item>
        ))}
      </ListGroup>
    </div>
  );
};

export default OrchestrationDetailsPage;
