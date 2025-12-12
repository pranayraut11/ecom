package com.ecom.orchestrator.client.initiator;

import com.ecom.orchestrator.client.config.OrchestrationLoader;
import com.ecom.orchestrator.client.service.OrchestrationService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.ecom.orchestrator.client.constants.Constant.ORCHESTRATOR_EVENT;

@Component
@RequiredArgsConstructor
@Slf4j
public class InitiatorRegistrar {

    private final OrchestrationLoader loader;
    private final OrchestrationService orchestrationService;
    private final KafkaAdmin kafkaAdmin;

    @Value("${shared.topic:false}")
    private boolean sharedTopic;

    @Value("${orchestrator.advanced.mode}")
    private boolean advancedMode;

    @Value("${orchestrator.topic.partitions:3}")
    private int topicPartitions;

    @Value("${orchestrator.topic.replicas:1}")
    private short topicReplicas;

    @PostConstruct
    public void registerInitiator() {
        List<NewTopic> topicsToCreate = new ArrayList<>();

        loader.getConfig().getOrchestrations().forEach(orc -> {
            if ("initiator".equalsIgnoreCase(orc.getAs())) {
               log.info("✅ Detected Initiator YAML");
               log.info("Registering orchestration: {} type {}", orc.getOrchestrationName(), orc.getType() );
               orc.setSharedTopic(sharedTopic);

               if(sharedTopic) {
                   log.info("Using Shared Topic for Initiator Events");
                   // Create shared topic
                   topicsToCreate.add(createTopic(ORCHESTRATOR_EVENT));
                   orchestrationService.register(orc, ORCHESTRATOR_EVENT);
               } else {
                   if(advancedMode) {
                       log.info("Using Dedicated Topics for Each Step (DO and UNDO)");
                       // Create topic for each step in the orchestration - both DO and UNDO
                       if (orc.getSteps() != null && !orc.getSteps().isEmpty()) {
                           orc.getSteps().forEach(step -> {
                               // Create DO topic
                               String doTopic = "orchestrator." + orc.getOrchestrationName() + "." + step.getName() + ".do";
                               topicsToCreate.add(createTopic(doTopic));
                               log.info("Queued topic creation for DO operation: {}", doTopic);

                               // Create UNDO topic
                               String undoTopic = "orchestrator." + orc.getOrchestrationName() + "." + step.getName() + ".undo";
                               topicsToCreate.add(createTopic(undoTopic));
                               log.info("Queued topic creation for UNDO operation: {}", undoTopic);
                           });
                       }
                   }
                   orchestrationService.register(orc);
               }
            }
        });

        // Create all topics at once
        if (!topicsToCreate.isEmpty()) {
            createKafkaTopics(topicsToCreate);
        }
    }

    private NewTopic createTopic(String topicName) {
        return new NewTopic(topicName, topicPartitions, topicReplicas);
    }

    private void createKafkaTopics(List<NewTopic> topics) {
        try {
            kafkaAdmin.createOrModifyTopics(topics.toArray(new NewTopic[0]));
            topics.forEach(topic ->
                log.info("✅ Created/Verified Kafka topic: {} with {} partitions and {} replicas",
                    topic.name(), topic.numPartitions(), topic.replicationFactor())
            );
        } catch (Exception e) {
            log.error("❌ Error creating Kafka topics: {}", e.getMessage());
            log.warn("Topics will be auto-created on first use if auto.create.topics.enable=true on broker");
        }
    }
}
