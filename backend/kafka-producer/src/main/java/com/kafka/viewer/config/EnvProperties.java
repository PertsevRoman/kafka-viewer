package com.kafka.viewer.config;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;
import java.util.Properties;

public class EnvProperties extends Properties {
    @Override
    public synchronized Object put(Object key, Object value) {
        if (value instanceof String) {
            return super.put(key, resolvePropertyValue((String) value));
        }

        return super.put(key, value);
    }

    /**
     * Resolve property value from env variable or default value
     * 
     * @param propertyValue Property value
     * 
     * @return
     */
    private static String resolvePropertyValue(String propertyValue) {
        if (!Objects.isNull(propertyValue)) {
            ConfigPair configPair = ConfigPair.parsePair(propertyValue);

            if (!Objects.isNull(configPair)) {
                String defaultValue = configPair.getDefaultValue();
                String envName = configPair.getEnvName();

                if (!Objects.isNull(envName)) {
                    String envValue = System.getenv(envName);

                    if (!StringUtils.isEmpty(envValue)) {
                        return envValue;
                    } else {
                        if (!StringUtils.isEmpty(defaultValue)) {
                            return defaultValue;
                        }
                    }
                }
            } else {
                if (!StringUtils.isEmpty(propertyValue)) {
                    return propertyValue;
                }
            }
        }

        throw new IllegalArgumentException("Can't find property " + propertyValue);
    }
}
