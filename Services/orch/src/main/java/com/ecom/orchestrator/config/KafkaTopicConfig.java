package com.ecom.orchestrator.config;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class KafkaTopicConfig {

    public List<String> getAllTopics() {
        return Arrays.asList(
            "orchestrator.registration",
            "orchestrator.execution.start",
            "orchestrator.response.result"
        );
    }
}
