package com.loopers.domain.vo

import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
data class Price(
    @Column(name = "price")
    val value: Long,
) {
    init {
        require(value >= 0) { "가격은 0 이상이어야 합니다." }
    }

    companion object {
        val ZERO = Price(0)
    }
}
