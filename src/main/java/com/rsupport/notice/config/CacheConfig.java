package com.rsupport.notice.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.*;
import org.springframework.data.redis.cache.*;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.time.Duration;

@Configuration
@EnableCaching
public class CacheConfig {
    @Bean
    @Profile("!test")
    public CacheManager redisCacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration conf = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10));
        return RedisCacheManager.builder(connectionFactory).cacheDefaults(conf).build();
    }

    // 테스트에서는 SimpleCacheManager (in-memory) 사용 - 임베디드 Redis 없이도 테스트 가능
    @Bean
    @Profile("test")
    public CacheManager simpleCacheManager() {
        return new org.springframework.cache.concurrent.ConcurrentMapCacheManager("noticeDetail");
    }
}
