package com.loopers.support.cache.optimized

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class OptimizedCacheable(
    val type: String,
    val ttlSeconds: Long,
)
