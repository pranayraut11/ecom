package com.ecom.orchestrator.entity;

import com.ecom.orchestrator.enums.TransactionName;
import com.ecom.orchestrator.enums.WorkflowStepStatus;
import com.ecom.shared.common.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrchestratorTransactionEntity extends BaseEntity {

    private TransactionName transactionName;

    private WorkflowStepStatus workflowStepStatus;

    private String entityId;

}
