package com.ecom.orchestrator.client.dto;

import com.ecom.orchestrator.client.config.OrchestrationConfig;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public class ExecutionMessage {
    private Object payload;
    private OrchestrationConfig.Orchestration orchestration;
    private Map<String, Object> headers;

    public ExecutionMessage(Object payload) {
        this.payload = payload;
    }
}
