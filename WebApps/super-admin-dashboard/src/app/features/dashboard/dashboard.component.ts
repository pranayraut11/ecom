import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {
  adminName = 'Admin';
  tenantName = 'Default Tenant';
  tenantId = 'T-12345';

  // Dashboard statistics
  totalUsers = 1250;
  activeUsers = 734;
  activeSessions = 125;
  clients = 17;

  // Recent activity data
  recentActivities = [
    { user: 'John Doe', action: 'User created', timestamp: '2023-09-15 13:45', status: 'success' },
    { user: 'Jane Smith', action: 'Login failed', timestamp: '2023-09-15 11:32', status: 'danger' },
    { user: 'Admin', action: 'Tenant "Enterprise Co." created', timestamp: '2023-09-14 16:20', status: 'success' },
    { user: 'Mike Johnson', action: 'Role changed to Admin', timestamp: '2023-09-14 10:15', status: 'warning' },
    { user: 'Sarah Williams', action: 'Password updated', timestamp: '2023-09-13 09:45', status: 'success' }
  ];

  constructor() { }

  ngOnInit(): void {
  }

  // Methods for quick actions
  createTenant(): void {
    console.log('Create tenant action triggered');
    // Implementation for creating a tenant
  }

  addUser(): void {
    console.log('Add user action triggered');
    // Implementation for adding a user
  }

  manageClients(): void {
    console.log('Manage clients action triggered');
    // Implementation for managing clients
  }

  manageRoles(): void {
    console.log('Manage roles action triggered');
    // Implementation for managing roles
  }
}
