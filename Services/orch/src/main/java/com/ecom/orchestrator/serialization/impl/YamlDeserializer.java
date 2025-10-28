package com.ecom.orchestrator.serialization.impl;

import com.ecom.orchestrator.serialization.Deserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component("yamlDeserializer")
@Slf4j
public class YamlDeserializer<T> implements Deserializer<T> {

    private final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());

    @Override
    public T deserialize(byte[] data, Class<T> targetType) {
        try {
            return yamlMapper.readValue(data, targetType);
        } catch (Exception e) {
            log.error("Failed to deserialize YAML data to type: {}", targetType.getSimpleName(), e);
            throw new RuntimeException("YAML deserialization failed", e);
        }
    }
}
