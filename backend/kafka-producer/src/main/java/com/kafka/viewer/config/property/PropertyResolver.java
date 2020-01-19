package com.kafka.viewer.config.property;

/**
 */
public interface PropertyResolver {
    /**
     * Resolve property
     * @param key Property key
     * @param propertyValue Property value
     *
     * @return null - if property can't be resolved
     */
    String resolveProperty(String key, String propertyValue);
}
