package com.ecom.orchestrator.serialization;

/**
 * Generic deserializer interface
 */
public interface Deserializer<T> {
    T deserialize(byte[] data, Class<T> targetType);
}
