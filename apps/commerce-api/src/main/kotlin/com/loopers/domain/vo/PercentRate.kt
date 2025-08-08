package com.loopers.domain.vo

import jakarta.persistence.Embeddable

@Embeddable
data class PercentRate(
    val value: Double,
) {
    init {
        require(value in 0.0..100.0) { "할인율은 0.0 이상 100.0 이하의 값이어야 합니다." }
    }

    companion object {
        val ZERO = PercentRate(0.0)
    }
}
