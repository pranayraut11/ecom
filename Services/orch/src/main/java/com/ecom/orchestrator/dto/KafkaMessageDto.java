package com.ecom.orchestrator.dto;

import lombok.Data;

import java.util.Map;

@Data
public class KafkaMessageDto {
    private Object payload;
    private Map<String, Object> headers;

}
