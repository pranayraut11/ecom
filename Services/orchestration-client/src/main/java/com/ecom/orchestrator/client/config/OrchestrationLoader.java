package com.ecom.orchestrator.client.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
public class OrchestrationLoader {

    private OrchestrationConfig config;

    @PostConstruct
    public void load() throws Exception {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("orchestrations.yml")) {
            if (is == null) throw new RuntimeException("orchestrations.yml not found in classpath");
            config = mapper.readValue(is, OrchestrationConfig.class);
        }
    }

    public OrchestrationConfig getConfig() {
        return config;
    }
}
