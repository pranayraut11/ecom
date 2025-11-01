package com.ecom.orchestrator.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Optional;

/**
 * Utility class for safely extracting values from message headers or maps.
 * Provides type-safe methods with null handling and default values.
 */
@Slf4j
@UtilityClass
public class MessageHeaderUtils {

    /**
     * Extract a String value from a map by key.
     * Returns empty string if key doesn't exist or value is null.
     *
     * @param map the map to extract from
     * @param key the key to look up
     * @return the string value or empty string
     */
    public static String getString(Map<String, Object> map, String key) {
        return getString(map, key, "");
    }

    /**
     * Extract a String value from a map by key with a custom default value.
     *
     * @param map the map to extract from
     * @param key the key to look up
     * @param defaultValue the default value to return if key doesn't exist or value is null
     * @return the string value or default value
     */
    public static String getString(Map<String, Object> map, String key, String defaultValue) {
        if (map == null || key == null) {
            return defaultValue;
        }

        Object value = map.get(key);
        return convertToString(value).orElse(defaultValue);
    }

    /**
     * Extract a String value from an Object.
     * Handles String instances and converts other types to string.
     *
     * @param obj the object to convert
     * @return the string value or empty string
     */
    public static String getString(Object obj) {
        return convertToString(obj).orElse("");
    }

    /**
     * Convert an Object to String safely.
     *
     * @param obj the object to convert
     * @return Optional containing the string value, or empty if obj is null
     */
    private static Optional<String> convertToString(Object obj) {
        if (obj == null) {
            return Optional.empty();
        }

        if (obj instanceof String str) {
            return Optional.of(str);
        }

        return Optional.of(obj.toString());
    }

    /**
     * Extract a Boolean value from a map by key.
     * Returns false if key doesn't exist or value cannot be converted to boolean.
     *
     * @param map the map to extract from
     * @param key the key to look up
     * @return the boolean value or false
     */
    public static boolean getBoolean(Map<String, Object> map, String key) {
        return getBoolean(map, key, false);
    }

    /**
     * Extract a Boolean value from a map by key with a custom default value.
     *
     * @param map the map to extract from
     * @param key the key to look up
     * @param defaultValue the default value to return if key doesn't exist or value cannot be converted
     * @return the boolean value or default value
     */
    public static boolean getBoolean(Map<String, Object> map, String key, boolean defaultValue) {
        if (map == null || key == null) {
            return defaultValue;
        }

        Object value = map.get(key);
        return convertToBoolean(value).orElse(defaultValue);
    }

    /**
     * Extract a Boolean value from an Object.
     * Handles Boolean instances, String "true"/"false", and numeric values (0/1).
     *
     * @param obj the object to convert
     * @return the boolean value or false
     */
    public static boolean getBoolean(Object obj) {
        return convertToBoolean(obj).orElse(false);
    }

    /**
     * Convert an Object to Boolean safely.
     * Supports:
     * - Boolean instances
     * - String values: "true", "yes", "1", "on" (case-insensitive) -> true
     * - Numeric values: 1 -> true, 0 -> false
     *
     * @param obj the object to convert
     * @return Optional containing the boolean value, or empty if conversion fails
     */
    private static Optional<Boolean> convertToBoolean(Object obj) {
        if (obj == null) {
            return Optional.empty();
        }

        if (obj instanceof Boolean bool) {
            return Optional.of(bool);
        }

        if (obj instanceof String str) {
            String normalized = str.trim().toLowerCase();
            if ("true".equals(normalized) || "yes".equals(normalized) || "1".equals(normalized) || "on".equals(normalized)) {
                return Optional.of(true);
            }
            if ("false".equals(normalized) || "no".equals(normalized) || "0".equals(normalized) || "off".equals(normalized)) {
                return Optional.of(false);
            }
            log.warn("Cannot convert string '{}' to boolean, using default", str);
            return Optional.empty();
        }

        if (obj instanceof Number num) {
            return Optional.of(num.intValue() != 0);
        }

        log.warn("Cannot convert object of type {} to boolean, using default", obj.getClass().getSimpleName());
        return Optional.empty();
    }

    /**
     * Extract an Integer value from a map by key.
     * Returns null if key doesn't exist or value cannot be converted to integer.
     *
     * @param map the map to extract from
     * @param key the key to look up
     * @return the integer value or null
     */
    public static Integer getInteger(Map<String, Object> map, String key) {
        return getInteger(map, key, null);
    }

    /**
     * Extract an Integer value from a map by key with a custom default value.
     *
     * @param map the map to extract from
     * @param key the key to look up
     * @param defaultValue the default value to return if key doesn't exist or value cannot be converted
     * @return the integer value or default value
     */
    public static Integer getInteger(Map<String, Object> map, String key, Integer defaultValue) {
        if (map == null || key == null) {
            return defaultValue;
        }

        Object value = map.get(key);
        return convertToInteger(value).orElse(defaultValue);
    }

    /**
     * Convert an Object to Integer safely.
     *
     * @param obj the object to convert
     * @return Optional containing the integer value, or empty if conversion fails
     */
    private static Optional<Integer> convertToInteger(Object obj) {
        if (obj == null) {
            return Optional.empty();
        }

        if (obj instanceof Number num) {
            return Optional.of(num.intValue());
        }

        if (obj instanceof String str) {
            try {
                return Optional.of(Integer.parseInt(str.trim()));
            } catch (NumberFormatException e) {
                log.warn("Cannot convert string '{}' to integer", str);
                return Optional.empty();
            }
        }

        log.warn("Cannot convert object of type {} to integer", obj.getClass().getSimpleName());
        return Optional.empty();
    }

    /**
     * Check if a map contains a non-null value for a given key.
     *
     * @param map the map to check
     * @param key the key to look up
     * @return true if the map contains a non-null value for the key
     */
    public static boolean hasValue(Map<String, Object> map, String key) {
        return map != null && key != null && map.containsKey(key) && map.get(key) != null;
    }

    /**
     * Get a value from map or return a default value if not present.
     *
     * @param map the map to extract from
     * @param key the key to look up
     * @param defaultValue the default value
     * @param <T> the type of the value
     * @return the value or default
     */
    @SuppressWarnings("unchecked")
    public static <T> T getOrDefault(Map<String, Object> map, String key, T defaultValue) {
        if (map == null || key == null || !map.containsKey(key)) {
            return defaultValue;
        }

        Object value = map.get(key);
        if (value == null) {
            return defaultValue;
        }

        try {
            return (T) value;
        } catch (ClassCastException e) {
            log.warn("Cannot cast value for key '{}' to expected type, using default", key);
            return defaultValue;
        }
    }
}

