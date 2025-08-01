package com.loopers.domain.point.vo

@JvmInline
value class Point(
    val value: Long,
) {

    companion object {
        val ZERO = Point(0)
    }
}
