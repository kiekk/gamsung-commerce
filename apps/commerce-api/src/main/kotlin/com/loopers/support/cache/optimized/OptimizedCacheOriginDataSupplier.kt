package com.loopers.support.cache.optimized

fun interface OptimizedCacheOriginDataSupplier<T> {
    @Throws(Exception::class)
    fun get(): T
}
