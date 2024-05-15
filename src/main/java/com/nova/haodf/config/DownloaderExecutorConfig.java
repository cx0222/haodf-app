package com.nova.haodf.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class DownloaderExecutorConfig {
    @Value("${options.downloader-executor.core-pool-size}")
    private int corePoolSize;
    @Value("${options.downloader-executor.max-pool-size}")
    private int maxPoolSize;
    @Value("${options.downloader-executor.queue-capacity}")
    private int queueCapacity;
    @Value("${options.downloader-executor.await-termination-seconds}")
    private int awaitTerminationSeconds;
    @Value("${options.downloader-executor.thread-name-prefix}")
    private String threadNamePrefix;

    public ThreadPoolTaskExecutor getDownloaderExecutor() {
        ThreadPoolTaskExecutor downloaderExecutor = new ThreadPoolTaskExecutor();
        downloaderExecutor.setThreadNamePrefix(threadNamePrefix);
        downloaderExecutor.setCorePoolSize(corePoolSize);
        downloaderExecutor.setMaxPoolSize(maxPoolSize);
        downloaderExecutor.setQueueCapacity(queueCapacity);
        downloaderExecutor.setAwaitTerminationSeconds(awaitTerminationSeconds);
        downloaderExecutor.setWaitForTasksToCompleteOnShutdown(true);
        downloaderExecutor.initialize();
        return downloaderExecutor;
    }
}
