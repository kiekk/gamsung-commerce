package com.loopers.domain.vo

class Price(
    val value: Long,
) {
    init {
        require(value >= 0) { "가격은 0 이상이어야 합니다." }
    }
}
