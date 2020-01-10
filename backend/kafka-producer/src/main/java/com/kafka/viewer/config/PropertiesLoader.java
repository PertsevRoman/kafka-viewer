package com.kafka.viewer.config;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Collectors;

public class PropertiesLoader {
    

    /**
     * Raw properties store
     */
    private static Properties properties;

    /**
     * 
     */
    private static final String propertiesFilePath = "application.properties";

    static {
        properties = new EnvProperties();

        try (InputStream resourceAsStream = ClassLoader
                .getSystemClassLoader()
                .getResourceAsStream(propertiesFilePath)) {

            if (!Objects.isNull(resourceAsStream)) {
                properties.load(resourceAsStream);
            }
        } catch (IOException e) {
            System.err.println("Unable to load properties file : " + propertiesFilePath);
        }
    }

    public static Properties getProperties() {
        return properties;
    }
}
