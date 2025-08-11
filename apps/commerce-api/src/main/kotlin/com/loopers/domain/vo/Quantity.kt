package com.loopers.domain.vo

@JvmInline
value class Quantity(
    val value: Int,
) {
    init {
        require(value > 0) { "수량은 1 이상이어야 합니다." }
    }
}
