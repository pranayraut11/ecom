import React from 'react';
import { Link } from 'react-router-dom';

const Sidebar: React.FC = () => {
  return (
    <div className="bg-light border-end" style={{ width: '250px' }}>
      <div className="list-group list-group-flush">
        <Link to="/" className="list-group-item list-group-item-action">Dashboard</Link>
        <Link to="/orchestrations-page" className="list-group-item list-group-item-action">Orchestrations</Link>
        <Link to="/executions" className="list-group-item list-group-item-action">Executions</Link>
        <Link to="/self-healing" className="list-group-item list-group-item-action">Self-Healing</Link>
      </div>
    </div>
  );
};

export default Sidebar;