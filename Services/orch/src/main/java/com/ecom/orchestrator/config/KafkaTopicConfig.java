package com.ecom.orchestrator.config;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

import static com.ecom.orchestrator.constant.RegistrationConstants.ORCHESTRATOR_EVENT;

@Component
public class KafkaTopicConfig {

    public List<String> getAllTopics() {
        return Arrays.asList(

                ORCHESTRATOR_EVENT
        );
    }
}
