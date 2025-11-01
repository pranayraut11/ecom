package com.ecom.orchestrator.util;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for MessageHeaderUtils
 */
class MessageHeaderUtilsTest {

    @Test
    void testGetString_withValidValue() {
        Map<String, Object> map = new HashMap<>();
        map.put("key1", "value1");

        String result = MessageHeaderUtils.getString(map, "key1");

        assertEquals("value1", result);
    }

    @Test
    void testGetString_withNullValue() {
        Map<String, Object> map = new HashMap<>();
        map.put("key1", null);

        String result = MessageHeaderUtils.getString(map, "key1");

        assertEquals("", result);
    }

    @Test
    void testGetString_withMissingKey() {
        Map<String, Object> map = new HashMap<>();

        String result = MessageHeaderUtils.getString(map, "nonexistent");

        assertEquals("", result);
    }

    @Test
    void testGetString_withDefaultValue() {
        Map<String, Object> map = new HashMap<>();

        String result = MessageHeaderUtils.getString(map, "nonexistent", "default");

        assertEquals("default", result);
    }

    @Test
    void testGetString_withNonStringValue() {
        Map<String, Object> map = new HashMap<>();
        map.put("key1", 123);

        String result = MessageHeaderUtils.getString(map, "key1");

        assertEquals("123", result);
    }

    @Test
    void testGetBoolean_withBooleanValue() {
        Map<String, Object> map = new HashMap<>();
        map.put("key1", true);

        boolean result = MessageHeaderUtils.getBoolean(map, "key1", false);

        assertTrue(result);
    }

    @Test
    void testGetBoolean_withStringTrue() {
        Map<String, Object> map = new HashMap<>();
        map.put("key1", "true");

        boolean result = MessageHeaderUtils.getBoolean(map, "key1", false);

        assertTrue(result);
    }

    @Test
    void testGetBoolean_withStringYes() {
        Map<String, Object> map = new HashMap<>();
        map.put("key1", "yes");

        boolean result = MessageHeaderUtils.getBoolean(map, "key1", false);

        assertTrue(result);
    }

    @Test
    void testGetBoolean_withNumber() {
        Map<String, Object> map = new HashMap<>();
        map.put("key1", 1);

        boolean result = MessageHeaderUtils.getBoolean(map, "key1", false);

        assertTrue(result);
    }

    @Test
    void testGetBoolean_withZero() {
        Map<String, Object> map = new HashMap<>();
        map.put("key1", 0);

        boolean result = MessageHeaderUtils.getBoolean(map, "key1", true);

        assertFalse(result);
    }

    @Test
    void testGetBoolean_withDefaultValue() {
        Map<String, Object> map = new HashMap<>();

        boolean result = MessageHeaderUtils.getBoolean(map, "nonexistent", true);

        assertTrue(result);
    }

    @Test
    void testGetInteger_withIntegerValue() {
        Map<String, Object> map = new HashMap<>();
        map.put("key1", 42);

        Integer result = MessageHeaderUtils.getInteger(map, "key1");

        assertEquals(42, result);
    }

    @Test
    void testGetInteger_withStringValue() {
        Map<String, Object> map = new HashMap<>();
        map.put("key1", "123");

        Integer result = MessageHeaderUtils.getInteger(map, "key1");

        assertEquals(123, result);
    }

    @Test
    void testGetInteger_withInvalidString() {
        Map<String, Object> map = new HashMap<>();
        map.put("key1", "invalid");

        Integer result = MessageHeaderUtils.getInteger(map, "key1", 999);

        assertEquals(999, result);
    }

    @Test
    void testHasValue_withValidValue() {
        Map<String, Object> map = new HashMap<>();
        map.put("key1", "value1");

        boolean result = MessageHeaderUtils.hasValue(map, "key1");

        assertTrue(result);
    }

    @Test
    void testHasValue_withNullValue() {
        Map<String, Object> map = new HashMap<>();
        map.put("key1", null);

        boolean result = MessageHeaderUtils.hasValue(map, "key1");

        assertFalse(result);
    }

    @Test
    void testHasValue_withMissingKey() {
        Map<String, Object> map = new HashMap<>();

        boolean result = MessageHeaderUtils.hasValue(map, "nonexistent");

        assertFalse(result);
    }

    @Test
    void testGetOrDefault_withValidValue() {
        Map<String, Object> map = new HashMap<>();
        map.put("key1", "value1");

        String result = MessageHeaderUtils.getOrDefault(map, "key1", "default");

        assertEquals("value1", result);
    }

    @Test
    void testGetOrDefault_withMissingKey() {
        Map<String, Object> map = new HashMap<>();

        String result = MessageHeaderUtils.getOrDefault(map, "nonexistent", "default");

        assertEquals("default", result);
    }
}

