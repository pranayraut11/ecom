import React from 'react';
import { useParams } from 'react-router-dom';
import ExecutionList from '../components/ExecutionList';

const OrchestrationExecutions: React.FC = () => {
  const { orchName } = useParams<{ orchName: string }>();

  return (
    <div className="container mt-4">
      <ExecutionList orchName={orchName!} />
    </div>
  );
};

export default OrchestrationExecutions;