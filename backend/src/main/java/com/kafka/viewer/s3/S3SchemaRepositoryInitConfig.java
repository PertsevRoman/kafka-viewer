package com.kafka.viewer.s3;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.PostConstruct;

@Configuration
@EnableScheduling
@Profile("s3")
public class S3SchemaRepositoryInitConfig {


    @Autowired
    private S3RepositoryService s3RepositoryService;

    @PostConstruct
    private void init() {
        s3RepositoryService.initRepoSchemas();
    }

    /**
     * Scheduled updating
     */
    @Scheduled(cron = "${scheduler.s3-repo.cron}")
    private void scheduledUpdate() {
        s3RepositoryService.initRepoSchemas();
    }
}
