package com.ecom.order.util;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Utility class for handling internationalized messages
 */
public class MessageUtil {
    private static final String BUNDLE_NAME = "messages";
    private static ResourceBundle resourceBundle;

    static {
        // Initialize with default locale
        resourceBundle = ResourceBundle.getBundle(BUNDLE_NAME, Locale.getDefault());
    }

    /**
     * Get a message from the resource bundle
     * 
     * @param key The message key
     * @return The message
     */
    public static String getMessage(String key) {
        try {
            return resourceBundle.getString(key);
        } catch (MissingResourceException e) {
            return '!' + key + '!';
        }
    }

    /**
     * Get a message from the resource bundle and format it with parameters
     * 
     * @param key The message key
     * @param params The parameters to format the message with
     * @return The formatted message
     */
    public static String getMessage(String key, Object... params) {
        try {
            String pattern = resourceBundle.getString(key);
            return MessageFormat.format(pattern, params);
        } catch (MissingResourceException e) {
            return '!' + key + '!';
        }
    }

    /**
     * Change the locale for messages
     * 
     * @param locale The new locale
     */
    public static void setLocale(Locale locale) {
        resourceBundle = ResourceBundle.getBundle(BUNDLE_NAME, locale);
    }
}
