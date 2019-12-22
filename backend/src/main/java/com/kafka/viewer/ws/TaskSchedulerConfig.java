package com.kafka.viewer.ws;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

// TODO Monkey patch fix. Why should we declare scheduler w/o sockJS using  
@Configuration
public class TaskSchedulerConfig {
    @Bean
    public TaskScheduler sockJSTaskScheduler() {
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(3);
        threadPoolTaskScheduler.setThreadNamePrefix("app-timer-");
        threadPoolTaskScheduler.setDaemon(true);

        return threadPoolTaskScheduler;
    }
}
