import React, { useState } from 'react';
import { Link, useLocation } from 'react-router-dom';
import { Button } from 'react-bootstrap';
import './Sidebar.scss';

const Sidebar: React.FC = () => {
  const [isCollapsed, setIsCollapsed] = useState(false);
  const location = useLocation();

  const menuItems = [
    { path: '/', icon: 'bi-speedometer2', label: 'Dashboard' },
    { path: '/orchestrations', icon: 'bi-diagram-3', label: 'Orchestrations' },
    { path: '/executions', icon: 'bi-play-circle', label: 'Executions' },
    { path: '/self-healing', icon: 'bi-heart-pulse', label: 'Self-Healing' },
  ];

  return (
    <div className={`sidebar ${isCollapsed ? 'collapsed' : ''}`}>
      <div className="sidebar-header">
        <h3 className="brand">{!isCollapsed && 'Orchestrator'}</h3>
        <Button
          variant="link"
          className="toggle-btn"
          onClick={() => setIsCollapsed(!isCollapsed)}
        >
          <i className={`bi ${isCollapsed ? 'bi-chevron-right' : 'bi-chevron-left'}`}></i>
        </Button>
      </div>

      <div className="sidebar-content">
        {menuItems.map((item) => (
          <Link
            key={item.path}
            to={item.path}
            className={`sidebar-item ${location.pathname === item.path ? 'active' : ''}`}
          >
            <i className={`bi ${item.icon}`}></i>
            {!isCollapsed && <span>{item.label}</span>}
          </Link>
        ))}
      </div>

      <div className="sidebar-footer">
        <Button
          variant="link"
          className="theme-toggle"
          onClick={() => document.body.classList.toggle('dark-theme')}
        >
          <i className="bi bi-moon"></i>
          {!isCollapsed && <span>Toggle Theme</span>}
        </Button>
      </div>
    </div>
  );
};

export default Sidebar;