package com.ecom.orchestrator.serialization;

/**
 * Generic serializer interface
 */
public interface Serializer<T> {
    byte[] serialize(T object);
}
