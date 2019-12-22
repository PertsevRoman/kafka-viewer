package com.kafka.viewer.mvc;

import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.ErrorPageRegistrar;
import org.springframework.boot.web.server.ErrorPageRegistry;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@Profile("spa")
public class PageNotFoundController extends StaticHandler implements ErrorPageRegistrar {
    @Override
    public void registerErrorPages(ErrorPageRegistry registry) {
        ErrorPage errorPage = new ErrorPage(
                HttpStatus.NOT_FOUND, getResourcesLocation() + "index.html"
        );

        registry.addErrorPages(errorPage);
    }
}
