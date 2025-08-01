package com.loopers.domain.vo

import jakarta.persistence.Column
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.ResolverStyle

@JvmInline
value class Birthday(
    @Column(name = "birth_day")
    val value: String,
) {
    init {
        !value.matches(BIRTH_DATE_PATTERN) &&
                throw IllegalArgumentException("생년월일 형식이 올바르지 않습니다. (yyyy-MM-dd)")

        runCatching { LocalDate.parse(value, BIRTH_DATE_FORMATTER) }
            .onFailure { throw IllegalArgumentException("존재하지 않는 생년월일입니다. ($value)") }
    }

    companion object {
        private val BIRTH_DATE_PATTERN = "^\\d{4}-\\d{2}-\\d{2}$".toRegex()
        private val BIRTH_DATE_FORMATTER = DateTimeFormatter.ofPattern("uuuu-MM-dd").withResolverStyle(ResolverStyle.STRICT)
    }
}
