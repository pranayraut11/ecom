package com.ecom.orchestrator.client.initiator;

import com.ecom.orchestrator.client.config.OrchestrationLoader;
import com.ecom.orchestrator.client.service.OrchestrationService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class InitiatorRegistrar {

    private final OrchestrationLoader loader;
    private final OrchestrationService orchestrationService;

    @PostConstruct
    public void registerInitiator() {
        loader.getConfig().getOrchestrations().forEach(orc -> {
            if ("initiator".equalsIgnoreCase(orc.getAs())) {
               log.info("✅ Detected Initiator YAML");
               log.info("Registering orchestration: {} type {}", orc.getOrchestrationName(), orc.getType() );
               orchestrationService.register(orc);
            }
        });
    }
}
