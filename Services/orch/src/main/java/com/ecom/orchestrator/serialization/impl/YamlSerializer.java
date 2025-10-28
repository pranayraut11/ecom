package com.ecom.orchestrator.serialization.impl;

import com.ecom.orchestrator.serialization.Serializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component("yamlSerializer")
@Slf4j
public class YamlSerializer<T> implements Serializer<T> {

    private final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());

    @Override
    public byte[] serialize(T object) {
        try {
            return yamlMapper.writeValueAsBytes(object);
        } catch (Exception e) {
            log.error("Failed to serialize object to YAML: {}", object, e);
            throw new RuntimeException("YAML serialization failed", e);
        }
    }
}
