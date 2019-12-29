package com.kafka.viewer.config;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;

public class PropertiesLoader {
    private static Properties properties;

    static {
        properties = new Properties();

        String filePath = "application.properties";

        try (InputStream resourceAsStream = ClassLoader
                .getSystemClassLoader()
                .getResourceAsStream(filePath)) {

            if (!Objects.isNull(resourceAsStream)) {
                properties.load(resourceAsStream);
            }
        } catch (IOException e) {
            System.err.println("Unable to load properties file : " + filePath);
        }
    }
    
    public static String getProperty(String name) {
        return resolvePropertyValue(properties.getProperty(name));
    }

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
        return null;
    }
}
