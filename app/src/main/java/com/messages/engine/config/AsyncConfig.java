package com.messages.engine.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Configuration for asynchronous task execution.
 */
@Configuration
@EnableAsync
public class AsyncConfig {

    /**
     * Creates a custom TaskExecutor for heavy asynchronous tasks.
     *
     * @return a configured TaskExecutor.
     */
    @Bean(name = "heavyTaskExecutor")
    public TaskExecutor heavyTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(50);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("HeavyTask-");
        executor.initialize();
        return executor;
    }

}
