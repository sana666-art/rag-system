package com.rag_system.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.task.DelegatingSecurityContextAsyncTaskExecutor;

/**
 * Dedicated executor for streaming LLM work so the blocking
 * Gemini read and SSE writes never run on a servlet thread.
 * Wrapped in DelegatingSecurityContextAsyncTaskExecutor so the
 * authenticated SecurityContext propagates to sse-stream-* threads.
 */
@Configuration
public class AsyncConfig {

    @Bean(name = "sseStreamExecutor")
    public TaskExecutor sseStreamExecutor() {

        ThreadPoolTaskExecutor delegate = new ThreadPoolTaskExecutor();
        delegate.setCorePoolSize(4);
        delegate.setMaxPoolSize(8);
        delegate.setQueueCapacity(100);
        delegate.setThreadNamePrefix("sse-stream-");
        delegate.setWaitForTasksToCompleteOnShutdown(false);
        delegate.initialize();

        return new DelegatingSecurityContextAsyncTaskExecutor(delegate);
    }
}
