package com.loopers.domain.vo

import jakarta.persistence.Column

@JvmInline
value class Email(
    @Column(name = "email")
    val value: String,
) {
    init {
        !value.matches(EMAIL_PATTERN) &&
                throw IllegalArgumentException("이메일 형식이 올바르지 않습니다.")
    }

    companion object {
        private val EMAIL_PATTERN = "^(?!.*\\.\\.)[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
    }
}
