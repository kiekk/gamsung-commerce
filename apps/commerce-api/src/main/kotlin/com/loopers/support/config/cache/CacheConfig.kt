package com.loopers.support.config.cache

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.loopers.support.config.cache.CacheConfig.CacheNames.BRAND_DETAIL
import com.loopers.support.config.cache.CacheConfig.CacheNames.PRODUCT_DETAIL
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer
import java.time.Duration

@Configuration
@EnableCaching
class CacheConfig {

    @Bean
    fun redisCacheManager(connectionFactory: RedisConnectionFactory): RedisCacheManager {
        val objectMapper = ObjectMapper()
            .registerModule(JavaTimeModule())
            .registerKotlinModule()  // 코틀린 지원 추가
            .registerModule(JavaTimeModule()) // 날짜 및 시간 객체 지원 추가
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .activateDefaultTyping(
                BasicPolymorphicTypeValidator.builder().allowIfBaseType(Any::class.java).build(),
                ObjectMapper.DefaultTyping.EVERYTHING,
            )
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        // config
        val redisCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofSeconds(10))
            .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(StringRedisSerializer()))
            .serializeValuesWith(
                RedisSerializationContext.SerializationPair.fromSerializer(GenericJackson2JsonRedisSerializer(objectMapper)),
            )
            .disableCachingNullValues()
            .entryTtl(Duration.ofSeconds(10))
        // 캐시 이름마다 다른 TTL 설정 Option
        val cacheConfigurations = mapOf(
            PRODUCT_DETAIL to redisCacheConfig.entryTtl(Duration.ofSeconds(30)),
            BRAND_DETAIL to redisCacheConfig.entryTtl(Duration.ofSeconds(30)),
            CacheNames.PRODUCT_LIKE_COUNT to redisCacheConfig.entryTtl(Duration.ofSeconds(30)),
        )

        // build CacheManager
        return RedisCacheManager.builder(connectionFactory)
            .cacheDefaults(redisCacheConfig)
            .withInitialCacheConfigurations(cacheConfigurations)
            .build()
    }

    object CacheNames {
        const val PRODUCT_FACADE_DETAIL = "product:facade:detail"
        const val PRODUCT_DETAIL = "product:detail"
        const val BRAND_DETAIL = "brand:detail"
        const val PRODUCT_LIKE_COUNT = "product:like:count"
    }
}
