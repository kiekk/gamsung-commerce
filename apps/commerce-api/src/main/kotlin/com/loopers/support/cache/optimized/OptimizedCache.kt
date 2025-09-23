package com.loopers.support.cache.optimized

import DataSerializer
import com.fasterxml.jackson.annotation.JsonIgnore
import java.time.Duration
import java.time.LocalDateTime

class OptimizedCache private constructor(
    val data: String,
    val expiredAt: LocalDateTime,
) {

    override fun toString(): String {
        return "OptimizedCache(expiredAt=$expiredAt, data=$data)"
    }

    @JsonIgnore
    fun isExpired(): Boolean {
        return LocalDateTime.now().isAfter(expiredAt)
    }

    fun <T> parseData(dataType: Class<T>): T? {
        return DataSerializer.deserialize(data, dataType)
    }

    companion object {
        fun of(data: Any?, ttl: Duration): OptimizedCache {
            return OptimizedCache(
                DataSerializer.serialize(data),
                LocalDateTime.now().plus(ttl),
            )
        }
    }
}
