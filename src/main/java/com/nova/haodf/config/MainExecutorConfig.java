package com.nova.haodf.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class MainExecutorConfig {
    @Value("${options.main-executor.core-pool-size}")
    private int corePoolSize;
    @Value("${options.main-executor.max-pool-size}")
    private int maxPoolSize;
    @Value("${options.main-executor.queue-capacity}")
    private int queueCapacity;
    @Value("${options.main-executor.await-termination-seconds}")
    private int awaitTerminationSeconds;
    @Value("${options.main-executor.thread-name-prefix}")
    private String threadNamePrefix;

    public ThreadPoolTaskExecutor getMainExecutor() {
        ThreadPoolTaskExecutor mainExecutor = new ThreadPoolTaskExecutor();
        mainExecutor.setThreadNamePrefix(threadNamePrefix);
        mainExecutor.setCorePoolSize(corePoolSize);
        mainExecutor.setMaxPoolSize(maxPoolSize);
        mainExecutor.setQueueCapacity(queueCapacity);
        mainExecutor.setAwaitTerminationSeconds(awaitTerminationSeconds);
        mainExecutor.setWaitForTasksToCompleteOnShutdown(true);
        mainExecutor.initialize();
        return mainExecutor;
    }
}
