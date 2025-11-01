import React, { useState } from 'react';
import { Link, useLocation } from 'react-router-dom';
import { Navbar as BSNavbar, Nav, Container, Dropdown } from 'react-bootstrap';

const Navbar: React.FC = () => {
  const location = useLocation();
  const [expanded, setExpanded] = useState(false);

  const isActive = (path: string) => {
    if (path === '/') {
      return location.pathname === '/';
    }
    if (path === '/orchestrations') {
      return location.pathname === '/orchestrations' ||
             location.pathname.startsWith('/orchestrations/');
    }
    return location.pathname === path || location.pathname.startsWith(path + '/');
  };

  return (
    <BSNavbar
      bg="dark"
      variant="dark"
      expand="lg"
      className="shadow-sm sticky-top"
      expanded={expanded}
      onToggle={setExpanded}
    >
      <Container fluid>
        {/* Brand Logo */}
        <BSNavbar.Brand as={Link} to="/" className="d-flex align-items-center">
          <i className="bi bi-diagram-3 me-2" style={{ fontSize: '1.5rem' }}></i>
          <span className="fw-bold">Orchestrator UI</span>
        </BSNavbar.Brand>

        <BSNavbar.Toggle aria-controls="navbar-nav" />

        <BSNavbar.Collapse id="navbar-nav">
          {/* Main Navigation */}
          <Nav className="me-auto">
            <Nav.Link
              as={Link}
              to="/"
              active={isActive('/')}
              onClick={() => setExpanded(false)}
              className="d-flex align-items-center px-3"
            >
              <i className="bi bi-speedometer2 me-2"></i>
              Dashboard
            </Nav.Link>

            <Nav.Link
              as={Link}
              to="/orchestrations"
              active={isActive('/orchestrations')}
              onClick={() => setExpanded(false)}
              className="d-flex align-items-center px-3"
            >
              <i className="bi bi-diagram-3 me-2"></i>
              Orchestrations
            </Nav.Link>

            <Nav.Link
              as={Link}
              to="/executions"
              active={isActive('/executions')}
              onClick={() => setExpanded(false)}
              className="d-flex align-items-center px-3"
            >
              <i className="bi bi-play-circle me-2"></i>
              Executions
            </Nav.Link>

            <Nav.Link
              as={Link}
              to="/self-healing"
              active={isActive('/self-healing')}
              onClick={() => setExpanded(false)}
              className="d-flex align-items-center px-3"
            >
              <i className="bi bi-wrench-adjustable me-2"></i>
              Self-Healing
            </Nav.Link>
          </Nav>

          {/* Right Side Items */}
          <Nav className="ms-auto align-items-center">
            {/* Notifications */}
            <Dropdown align="end" className="me-2">
              <Dropdown.Toggle
                variant="outline-light"
                size="sm"
                className="rounded-circle p-2 border-0"
                style={{ width: '40px', height: '40px' }}
              >
                <i className="bi bi-bell"></i>
              </Dropdown.Toggle>
              <Dropdown.Menu>
                <Dropdown.Header>Notifications</Dropdown.Header>
                <Dropdown.Item>
                  <div className="d-flex align-items-start">
                    <i className="bi bi-check-circle text-success me-2 mt-1"></i>
                    <div>
                      <div className="small fw-semibold">Execution Complete</div>
                      <div className="small text-muted">TenantOnboarding finished</div>
                    </div>
                  </div>
                </Dropdown.Item>
                <Dropdown.Divider />
                <Dropdown.Item className="text-center small text-primary">
                  View All Notifications
                </Dropdown.Item>
              </Dropdown.Menu>
            </Dropdown>

            {/* User Profile */}
            <Dropdown align="end">
              <Dropdown.Toggle
                variant="outline-light"
                size="sm"
                className="d-flex align-items-center border-0"
              >
                <div
                  className="rounded-circle bg-primary d-flex align-items-center justify-content-center me-2"
                  style={{ width: '32px', height: '32px' }}
                >
                  <i className="bi bi-person-fill text-white"></i>
                </div>
                <span className="d-none d-md-inline">Admin</span>
              </Dropdown.Toggle>
              <Dropdown.Menu>
                <Dropdown.Header>Account Settings</Dropdown.Header>
                <Dropdown.Item>
                  <i className="bi bi-person me-2"></i>
                  Profile
                </Dropdown.Item>
                <Dropdown.Item>
                  <i className="bi bi-gear me-2"></i>
                  Settings
                </Dropdown.Item>
                <Dropdown.Divider />
                <Dropdown.Item className="text-danger">
                  <i className="bi bi-box-arrow-right me-2"></i>
                  Logout
                </Dropdown.Item>
              </Dropdown.Menu>
            </Dropdown>
          </Nav>
        </BSNavbar.Collapse>
      </Container>
    </BSNavbar>
  );
};

export default Navbar;
