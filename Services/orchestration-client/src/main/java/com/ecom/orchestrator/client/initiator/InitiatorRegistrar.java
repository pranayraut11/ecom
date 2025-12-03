package com.ecom.orchestrator.client.initiator;

import com.ecom.orchestrator.client.config.OrchestrationLoader;
import com.ecom.orchestrator.client.service.OrchestrationService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static com.ecom.orchestrator.client.constants.Constant.ORCHESTRATOR_EVENT;

@Component
@RequiredArgsConstructor
@Slf4j
public class InitiatorRegistrar {

    private final OrchestrationLoader loader;
    private final OrchestrationService orchestrationService;

    @Value("${shared.topic:false}")
    private boolean sharedTopic;

    @PostConstruct
    public void registerInitiator() {
        loader.getConfig().getOrchestrations().forEach(orc -> {
            if ("initiator".equalsIgnoreCase(orc.getAs())) {
               log.info("✅ Detected Initiator YAML");
               log.info("Registering orchestration: {} type {}", orc.getOrchestrationName(), orc.getType() );
               orc.setSharedTopic(sharedTopic);
               if(sharedTopic) {
                   log.info("Using Shared Topic for Initiator Events");
                   orchestrationService.register(orc, ORCHESTRATOR_EVENT);
               } else {
                   log.info("Using Dedicated Topic for Initiator Events");
                   orchestrationService.register(orc);
               }
            }
        });
    }
}
