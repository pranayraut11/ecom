package com.ecom.orchestrator.messaging.kafka;

import com.ecom.orchestrator.messaging.interfaces.TopicManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ExecutionException;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaTopicManager implements TopicManager {

    private final AdminClient adminClient;

    @Override
    public boolean topicExists(String topic) {
        try {
            ListTopicsResult listTopicsResult = adminClient.listTopics();
            Set<String> topics = listTopicsResult.names().get();
            return topics.contains(topic);
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error checking if topic exists: {}", topic, e);
            return false;
        }
    }

    @Override
    public void createTopic(String topic) {
        if (topicExists(topic)) {
            log.debug("Topic already exists: {}", topic);
            return;
        }

        try {
            NewTopic newTopic = new NewTopic(topic, 3, (short) 1);
            CreateTopicsResult result = adminClient.createTopics(Collections.singletonList(newTopic));
            result.all().get();
            log.info("Created topic: {}", topic);
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error creating topic: {}", topic, e);
            throw new RuntimeException("Failed to create topic: " + topic, e);
        }
    }
}
