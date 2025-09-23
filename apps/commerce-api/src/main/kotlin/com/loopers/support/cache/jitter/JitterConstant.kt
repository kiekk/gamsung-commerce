package com.loopers.support.cache.jitter

import java.time.Duration

class JitterConstant {
    companion object {
        const val JITTER_PERCENTAGE = 0.15
        val MINIMUM_TTL_MINUTES = Duration.ofSeconds(30)
    }
}
