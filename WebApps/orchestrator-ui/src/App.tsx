import React from 'react';
import { Routes, Route } from 'react-router-dom';
import Navbar from './components/Navbar';
import Sidebar from './components/Sidebar';
import Dashboard from './pages/Dashboard';
import Orchestrations from './pages/Orchestrations';
import Executions from './pages/Executions';
import SelfHealing from './pages/SelfHealing';
import OrchestrationsPage from './pages/OrchestrationsPage';
import OrchestrationDetailsPage from './pages/OrchestrationDetailsPage';
import OrchestrationExecutions from './pages/OrchestrationExecutions';

const App: React.FC = () => {
  return (
    <div className="d-flex">
      <Sidebar />
      <div className="flex-grow-1">
        <Navbar />
        <main className="container mt-4">
          <Routes>
            <Route path="/" element={<Dashboard />} />
            <Route path="/orchestrations" element={<Orchestrations />} />
            <Route path="/executions" element={<Executions />} />
            <Route path="/self-healing" element={<SelfHealing />} />
            <Route path="/orchestrations-page" element={<OrchestrationsPage />} />
            <Route path="/orchestrations/:orchName" element={<OrchestrationDetailsPage />} />
            <Route path="/orchestrations/:orchName/executions" element={<OrchestrationExecutions />} />
          </Routes>
        </main>
      </div>
    </div>
  );
};

export default App;