package com.loopers.support.cache.jitter

import com.loopers.support.cache.jitter.JitterConstant.Companion.JITTER_PERCENTAGE
import com.loopers.support.cache.jitter.JitterConstant.Companion.MINIMUM_TTL_MINUTES
import java.time.Duration
import kotlin.random.Random

object JitteredTTLCalculator {
    fun jitteredTtl(baseTTL: Duration): Duration {
        val baseTTLMs = baseTTL.toMillis()
        val delta = (baseTTLMs * JITTER_PERCENTAGE).toLong() // 지터 최대 폭

        val offset = Random.nextLong(-delta, delta + 1)

        // MINIMUM_TTL_MINUTES으로 다시 계산, 5지만 5분으로 계산해야함
        val jittered = (baseTTLMs + offset).coerceAtLeast(MINIMUM_TTL_MINUTES.toMillis())

        return Duration.ofMillis(jittered)
    }
}
