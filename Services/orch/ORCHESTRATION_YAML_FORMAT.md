# Updated Orchestration YAML Format Guide

## Overview
This document provides the complete YAML format for orchestration configuration including DO and UNDO methods.

---

## Initiator Configuration Format

### Sequential Orchestration
```yaml
orchestrations:
  - orchestrationName: tenantCreation        # Unique name for the orchestration
    as: initiator                            # Role: initiator or worker
    type: sequential                         # Type: sequential or parallel
    steps:
      - seq: 1                               # Execution sequence (for sequential)
        name: createRealm                    # Unique step name
        objectType: String                   # Data type for the step
        doMethod: createRealm                # Method name for DO operation
        undoMethod: deleteRealm              # Method name for UNDO operation
        
      - seq: 2
        name: createClient
        objectType: String
        doMethod: createClient
        undoMethod: deleteClient
        
      - seq: 3
        name: assignPermissions
        objectType: String
        doMethod: assignPermissions
        undoMethod: removePermissions
```

### Parallel Orchestration
```yaml
orchestrations:
  - orchestrationName: userSetup
    as: initiator
    type: parallel                           # All steps execute simultaneously
    steps:
      - seq: 1
        name: createProfile
        objectType: UserDto
        doMethod: createUserProfile
        undoMethod: deleteUserProfile
        
      - seq: 2
        name: assignRoles
        objectType: RoleDto
        doMethod: assignUserRoles
        undoMethod: removeUserRoles
        
      - seq: 3
        name: sendNotification
        objectType: EmailDto
        doMethod: sendWelcomeEmail
        undoMethod: logEmailCancellation
```

---

## Worker Configuration Format

### Single Step Registration
```yaml
orchestrations:
  - orchestrationName: tenantCreation        # Must match initiator's name
    as: worker                               # Role: worker
    steps:
      - name: createRealm                    # Must match step name in initiator
        objectType: String                   # Must match initiator's objectType
        handlerClass: realmService           # Spring bean name
        handlerMethod: createRealmByEvent    # Method for DO operation
        undoHandlerMethod: deleteRealmByEvent # Method for UNDO operation
```

### Multiple Steps Registration (Same Service)
```yaml
orchestrations:
  - orchestrationName: tenantCreation
    as: worker
    steps:
      - name: createRealm
        objectType: String
        handlerClass: realmService
        handlerMethod: createRealmByEvent
        undoHandlerMethod: deleteRealmByEvent
        
      - name: createClient
        objectType: String
        handlerClass: clientService
        handlerMethod: createClientByEvent
        undoHandlerMethod: deleteClientByEvent
```

### Multiple Orchestrations (Same Service)
```yaml
orchestrations:
  # Worker for tenantCreation orchestration
  - orchestrationName: tenantCreation
    as: worker
    steps:
      - name: createRealm
        objectType: String
        handlerClass: realmService
        handlerMethod: createRealmByEvent
        undoHandlerMethod: deleteRealmByEvent
  
  # Worker for userSetup orchestration
  - orchestrationName: userSetup
    as: worker
    steps:
      - name: createProfile
        objectType: UserDto
        handlerClass: userService
        handlerMethod: createUserProfileByEvent
        undoHandlerMethod: deleteUserProfileByEvent
```

---

## Complete Example: E-Commerce Order Processing

### Initiator (Order Service)
```yaml
orchestrations:
  - orchestrationName: orderProcessing
    as: initiator
    type: sequential
    steps:
      - seq: 1
        name: validateInventory
        objectType: OrderDto
        doMethod: checkInventory
        undoMethod: releaseInventory
        
      - seq: 2
        name: reserveInventory
        objectType: OrderDto
        doMethod: reserveStock
        undoMethod: unreserveStock
        
      - seq: 3
        name: processPayment
        objectType: PaymentDto
        doMethod: chargeCustomer
        undoMethod: refundCustomer
        
      - seq: 4
        name: createShipment
        objectType: ShipmentDto
        doMethod: scheduleDelivery
        undoMethod: cancelDelivery
        
      - seq: 5
        name: sendConfirmation
        objectType: NotificationDto
        doMethod: sendOrderConfirmation
        undoMethod: sendCancellationNotification
```

### Worker 1 (Inventory Service)
```yaml
orchestrations:
  - orchestrationName: orderProcessing
    as: worker
    steps:
      - name: validateInventory
        objectType: OrderDto
        handlerClass: inventoryService
        handlerMethod: validateInventoryHandler
        undoHandlerMethod: noOpHandler                # Some steps may not need undo
        
      - name: reserveInventory
        objectType: OrderDto
        handlerClass: inventoryService
        handlerMethod: reserveInventoryHandler
        undoHandlerMethod: unreserveInventoryHandler
```

### Worker 2 (Payment Service)
```yaml
orchestrations:
  - orchestrationName: orderProcessing
    as: worker
    steps:
      - name: processPayment
        objectType: PaymentDto
        handlerClass: paymentService
        handlerMethod: processPaymentHandler
        undoHandlerMethod: refundPaymentHandler
```

### Worker 3 (Shipping Service)
```yaml
orchestrations:
  - orchestrationName: orderProcessing
    as: worker
    steps:
      - name: createShipment
        objectType: ShipmentDto
        handlerClass: shippingService
        handlerMethod: createShipmentHandler
        undoHandlerMethod: cancelShipmentHandler
```

### Worker 4 (Notification Service)
```yaml
orchestrations:
  - orchestrationName: orderProcessing
    as: worker
    steps:
      - name: sendConfirmation
        objectType: NotificationDto
        handlerClass: emailService
        handlerMethod: sendConfirmationHandler
        undoHandlerMethod: sendCancellationHandler
```

---

## Field Descriptions

### Initiator Fields

| Field | Required | Description | Example |
|-------|----------|-------------|---------|
| `orchestrationName` | Yes | Unique identifier for the orchestration | `tenantCreation` |
| `as` | Yes | Role in orchestration | `initiator` |
| `type` | Yes | Execution type | `sequential` or `parallel` |
| `steps` | Yes | List of steps | See below |
| `steps[].seq` | Yes | Sequence number (determines order for sequential) | `1`, `2`, `3` |
| `steps[].name` | Yes | Unique step identifier | `createRealm` |
| `steps[].objectType` | Yes | Data type passed to step | `String`, `UserDto` |
| `steps[].doMethod` | Yes | Method name for DO operation | `createRealm` |
| `steps[].undoMethod` | Yes | Method name for UNDO operation | `deleteRealm` |

### Worker Fields

| Field | Required | Description | Example |
|-------|----------|-------------|---------|
| `orchestrationName` | Yes | Must match initiator's orchestration name | `tenantCreation` |
| `as` | Yes | Role in orchestration | `worker` |
| `steps` | Yes | List of steps this worker handles | See below |
| `steps[].name` | Yes | Must match step name in initiator | `createRealm` |
| `steps[].objectType` | Yes | Must match initiator's objectType | `String` |
| `steps[].handlerClass` | Yes | Spring bean name that handles this step | `realmService` |
| `steps[].handlerMethod` | Yes | Method to call for DO operation | `createRealmByEvent` |
| `steps[].undoHandlerMethod` | Yes | Method to call for UNDO operation | `deleteRealmByEvent` |

---

## Important Notes

### ✅ DO's
- Always provide both `doMethod` and `undoMethod` in initiator config
- Always provide both `handlerMethod` and `undoHandlerMethod` in worker config
- Use descriptive, action-based names for methods
- Ensure `objectType` matches between initiator and worker
- Keep step names consistent across initiator and workers
- For sequential orchestrations, set `seq` to determine execution order

### ❌ DON'Ts
- Don't skip `undoMethod` or `undoHandlerMethod` (required for rollback)
- Don't use different `objectType` between initiator and worker for same step
- Don't use duplicate step names within same orchestration
- Don't use duplicate sequence numbers in sequential orchestrations
- Don't change orchestration configuration without updating all related services

### 💡 Best Practices
1. **Naming Convention:**
   - DO method: `create*`, `update*`, `process*`, `send*`
   - UNDO method: `delete*`, `revert*`, `cancel*`, `remove*`

2. **Object Types:**
   - Use DTOs for complex data: `UserDto`, `OrderDto`
   - Use simple types for IDs: `String`, `Long`
   - Ensure DTOs are serializable

3. **Error Handling:**
   - Implement idempotent UNDO operations
   - Handle cases where resource doesn't exist during UNDO
   - Log all operations for debugging

4. **Testing:**
   - Test happy path (all steps succeed)
   - Test failure scenarios (each step fails)
   - Test UNDO operations work correctly
   - Verify parallel execution works as expected

---

## Migration from Old Format

### Old Format (Without UNDO)
```yaml
orchestrations:
  - orchestrationName: tenantCreation
    as: worker
    steps:
      - name: createRealm
        objectType: String
        handlerClass: realmService
        handlerMethod: createRealmByEvent
```

### New Format (With UNDO)
```yaml
orchestrations:
  - orchestrationName: tenantCreation
    as: worker
    steps:
      - name: createRealm
        objectType: String
        handlerClass: realmService
        handlerMethod: createRealmByEvent
        undoHandlerMethod: deleteRealmByEvent  # ADD THIS LINE
```

**Migration Steps:**
1. Add `undoMethod` to all initiator step configurations
2. Add `undoHandlerMethod` to all worker step configurations
3. Implement UNDO handler methods in worker services
4. Test UNDO functionality
5. Deploy updated configurations

---

## See Also
- **Implementation Guide:** `FAILURE_HANDLING_GUIDE.md`
- **Complete Examples:** `examples/orchestration-complete-example.yaml`
- **DO-UNDO Documentation:** `DO-UNDO-ORCHESTRATION.md`

