package com.kafka.viewer.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.view.InternalResourceView;
import org.springframework.web.servlet.view.UrlBasedViewResolver;

@Configuration
@Profile("spa")
public class SpaMvcConfiguration extends BasicConfiguration {
    @Value("${resources.location}")
    private String resourcesLocation;

    String getResourcesLocation() {
        if (!resourcesLocation.endsWith("/")) {
            return resourcesLocation + "/";
        }

        return resourcesLocation;
    }

    @Override
    public ViewResolver mvcViewResolver() {
        UrlBasedViewResolver viewResolver = new UrlBasedViewResolver();
        viewResolver.setViewClass(InternalResourceView.class);
        return viewResolver;
    }

    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        String staticServePath = "file:" + getResourcesLocation();

        registry
                .addResourceHandler("/**")
                .addResourceLocations(staticServePath);
        super.addResourceHandlers(registry);
    }
}
