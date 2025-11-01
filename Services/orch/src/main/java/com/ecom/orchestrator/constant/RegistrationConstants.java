package com.ecom.orchestrator.constant;

/**
 * Constants used in orchestration registration process
 */
public final class RegistrationConstants {

    private RegistrationConstants() {
        // Prevent instantiation
    }

    // Registration Roles
    public static final String ROLE_INITIATOR = "initiator";
    public static final String ROLE_WORKER = "worker";

    // Event Types
    public static final String EVENT_TYPE_REGISTRATION_STATUS = "REGISTRATION_STATUS";

    // Topics
    public static final String TOPIC_REGISTRATION_STATUS = "orchestrator.registration.status";

    // Topic Format
    public static final String TOPIC_NAME_FORMAT = "orchestrator.%s.%s";
}

