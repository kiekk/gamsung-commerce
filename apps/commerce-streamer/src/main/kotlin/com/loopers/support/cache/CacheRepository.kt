package com.loopers.support.cache

interface CacheRepository {
    fun <T> get(cacheKey: CacheKey, clazz: Class<T>): T?

    fun <T> set(cacheKey: CacheKey, value: T)

    fun evict(key: String)
}
