package com.kafka.viewer.mvc;

import org.springframework.beans.factory.annotation.Value;

public abstract class StaticHandler {
    @Value("${resources.location}")
    private String resourcesLocation;

    String getResourcesLocation() {
        if (!resourcesLocation.endsWith("/")) {
            return resourcesLocation + "/";
        }

        return resourcesLocation;
    }
}
