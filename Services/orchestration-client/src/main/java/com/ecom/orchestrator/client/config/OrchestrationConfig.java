package com.ecom.orchestrator.client.config;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class OrchestrationConfig {
    private List<Orchestration> orchestrations;

    @Setter
    @Getter
    public static class Orchestration {
        private String orchestrationName;
        private String as;
        private String type;
        private List<Step> steps;

    }

    @Setter
    @Getter
    public static class Step {
        private Integer seq;
        private String name;
        private String objectType;
        private String handlerClass;
        private String handlerMethod;  // Deprecated, use doMethod instead
        private String doMethod;
        private String undoMethod;

    }
}
