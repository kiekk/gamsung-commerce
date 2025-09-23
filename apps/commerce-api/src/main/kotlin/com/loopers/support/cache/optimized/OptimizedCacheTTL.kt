package com.loopers.support.cache.optimized

import java.time.Duration

class OptimizedCacheTTL private constructor(
    val logicalTTL: Duration,
    val physicalTTL: Duration,
) {

    override fun toString(): String {
        return "OptimizedCacheTTL(logicalTTL=${logicalTTL.toMillis()}, physicalTTL=${physicalTTL.toMillis()})"
    }

    companion object {
        const val PHYSICAL_TTL_DELAY_SECONDS: Long = 5

        fun of(logicalTTL: Duration): OptimizedCacheTTL {
            return OptimizedCacheTTL(
                logicalTTL,
                logicalTTL.plusSeconds(PHYSICAL_TTL_DELAY_SECONDS),
            )
        }
    }
}
