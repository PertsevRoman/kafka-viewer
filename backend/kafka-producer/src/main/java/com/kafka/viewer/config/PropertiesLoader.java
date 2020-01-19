package com.kafka.viewer.config;

import com.kafka.viewer.config.property.EnvironmentPropertyResolver;
import com.kafka.viewer.config.property.ResolvedProperties;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;

public class PropertiesLoader {
    

    /**
     * Raw properties store
     */
    private static ResolvedProperties properties;

    /**
     * 
     */
    private static final String propertiesFilePath = "application.properties";

    static {
        properties = new ResolvedProperties();

        final EnvironmentPropertyResolver environmentPropertyResolver =
                new EnvironmentPropertyResolver();

        properties.registerPropertyResolver(environmentPropertyResolver);

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
