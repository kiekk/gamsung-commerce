package com.loopers.domain.vo

import jakarta.persistence.Column

@JvmInline
value class Mobile(
    @Column(name = "mobile")
    val value: String,
) {
    init {
        !value.matches(MOBILE_PATTERN) &&
                throw IllegalArgumentException("전화번호 형식이 올바르지 않습니다.")
    }

    companion object {
        private val MOBILE_PATTERN = Regex("^01[0-9]-\\d{3,4}-\\d{4}$")
    }
}
