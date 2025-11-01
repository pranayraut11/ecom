import React from 'react';
import { Routes, Route } from 'react-router-dom';
import { AppProvider } from '@context';
import { ErrorBoundary } from '@components/ErrorBoundary';
import { ToastNotification } from '@components/ToastNotification';
import Navbar from './components/Navbar';
import Dashboard from './pages/Dashboard';
import Executions from './pages/Executions';
import SelfHealing from './pages/SelfHealing';
import OrchestrationsPage from './pages/OrchestrationsPage';
import OrchestrationDetailsPage from './pages/OrchestrationDetailsPage';
import ExecutionDetailsPage from './pages/ExecutionDetailsPage';

const App: React.FC = () => {
  return (
    <ErrorBoundary>
      <AppProvider>
        <ToastNotification />
        <div className="d-flex flex-column min-vh-100">
          <Navbar />
          <main className="flex-grow-1 bg-light">
            <Routes>
              <Route path="/" element={<Dashboard />} />
              <Route path="/orchestrations" element={<OrchestrationsPage />} />
              <Route path="/orchestrations/:orchName" element={<OrchestrationDetailsPage />} />
              <Route path="/orchestrations/:orchName/executions/:executionId" element={<ExecutionDetailsPage />} />
              <Route path="/executions" element={<Executions />} />
              <Route path="/executions/:executionId" element={<ExecutionDetailsPage />} />
              <Route path="/self-healing" element={<SelfHealing />} />
            </Routes>
          </main>
        </div>
      </AppProvider>
    </ErrorBoundary>
  );
};

export default App;

