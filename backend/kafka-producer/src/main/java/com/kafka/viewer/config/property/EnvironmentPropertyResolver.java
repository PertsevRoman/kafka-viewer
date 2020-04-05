package com.kafka.viewer.config.property;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * Resolve property from environment variables
 */
public class EnvironmentPropertyResolver implements PropertyResolver {

    @Override
    public String resolveProperty(String key, String propertyValue) {

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
            }
        }

        return null;
    }
}
