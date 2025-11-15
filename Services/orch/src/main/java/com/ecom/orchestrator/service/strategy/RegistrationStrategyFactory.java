package com.ecom.orchestrator.service.strategy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Factory to get the appropriate registration strategy based on role
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RegistrationStrategyFactory {

    private final List<RegistrationStrategy> strategies;
    private Map<String, RegistrationStrategy> strategyMap;

    /**
     * Get the strategy for the given role
     * 
     * @param role The registration role (initiator/worker)
     * @return The appropriate strategy
     * @throws IllegalArgumentException if no strategy found for role
     */
    public RegistrationStrategy getStrategy(String role) {
        if (strategyMap == null) {
            // Lazy initialization - create map of role -> strategy
            strategyMap = strategies.stream()
                    .collect(Collectors.toMap(
                            RegistrationStrategy::getRole,
                            Function.identity()
                    ));
            log.info("Initialized {} registration strategies", strategyMap.size());
        }

        RegistrationStrategy strategy = strategyMap.get(role.toLowerCase());
        if (strategy == null) {
            throw new IllegalArgumentException("No registration strategy found for role: " + role);
        }

        return strategy;
    }
}

