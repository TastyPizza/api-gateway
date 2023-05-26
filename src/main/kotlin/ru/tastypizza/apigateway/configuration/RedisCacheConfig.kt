package ru.tastypizza.apigateway.configuration

import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.connection.RedisConnectionFactory

@Configuration
@EnableCaching
class RedisCacheConfig {
    @Bean
     fun cacheManager(redisConnectionFactory : RedisConnectionFactory): RedisCacheManager {
        val cacheConfiguration : RedisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig ()
            .disableCachingNullValues();

        return RedisCacheManager.builder(redisConnectionFactory)
            .cacheDefaults(cacheConfiguration)
            .build();
    }
}