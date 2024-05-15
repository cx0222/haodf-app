package com.nova.haodf.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

import java.time.Duration;

@Configuration
public class RedisConfig {
    @Value("${options.cache.redis-host}")
    private String redisHost;
    @Value("${options.cache.redis-port}")
    private Integer redisPort;
    @Value("${options.cache.redis-default-entry-ttl-hours}")
    private Integer redisDefaultEntryTTlHours;

    @Bean
    public LettuceConnectionFactory lettuceConnectionFactory() {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration(redisHost, redisPort);
        return new LettuceConnectionFactory(configuration);
    }

    @Bean
    public RedisCacheManager redisCacheManager() {
        RedisCacheConfiguration configuration = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(redisDefaultEntryTTlHours));
        return RedisCacheManager.builder(lettuceConnectionFactory())
                .cacheDefaults(configuration)
                .build();
    }
}
