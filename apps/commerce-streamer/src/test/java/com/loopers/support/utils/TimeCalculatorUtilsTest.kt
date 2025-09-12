package com.loopers.support.utils

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime

class TimeCalculatorUtilsTest {
    @Test
    @DisplayName("days=2 - 25.09.11 11:50 실행 → 25.09.13 00:00 만료")
    fun calculateDurationByDays_beforeNoon() {
        // arrange
        val now = LocalDateTime.of(2025, 9, 11, 11, 50, 0)
        val expectedTarget = LocalDate.of(2025, 9, 13).atStartOfDay()
        val expected = Duration.between(now, expectedTarget)

        // act
        val actual = TimeCalculatorUtils.calculateDurationByDays(now, 2)

        // assert
        assertEquals(expected, actual)
    }

    @Test
    @DisplayName("days=2 - 25.09.11 12:10 실행 → 25.09.14 00:00 만료 (+1일)")
    fun calculateDurationByDays_afterNoon() {
        // arrange
        val now = LocalDateTime.of(2025, 9, 11, 12, 10, 0)
        val expectedTarget = LocalDate.of(2025, 9, 14).atStartOfDay()
        val expected = Duration.between(now, expectedTarget)

        // act
        val actual = TimeCalculatorUtils.calculateDurationByDays(now, 2)

        // assert
        assertEquals(expected, actual)
    }

    @Test
    @DisplayName("days=2 - 25.09.11 23:50 실행 → 25.09.14 00:00 만료 (+1일)")
    fun calculateDurationByDays_nearMidnight() {
        // arrange
        val now = LocalDateTime.of(2025, 9, 11, 23, 50, 0)
        val expectedTarget = LocalDate.of(2025, 9, 14).atStartOfDay()
        val expected = Duration.between(now, expectedTarget)

        // act
        val actual = TimeCalculatorUtils.calculateDurationByDays(now, 2)

        // assert
        assertEquals(expected, actual)
    }

    @Test
    @DisplayName("hours=2 - 01:50 실행 → 04:00 만료 (2h10m)")
    fun calculateDurationByHours_roundsUpToNextHour() {
        // arrange
        val now = LocalDateTime.of(2025, 9, 12, 1, 50, 0)
        val expectedTarget = LocalDateTime.of(2025, 9, 12, 4, 0, 0)
        val expected = Duration.between(now, expectedTarget)

        // act
        val actual = TimeCalculatorUtils.calculateDurationByHours(now, 2)

        // assert
        assertEquals(expected, actual)
    }

    @Test
    @DisplayName("hours=2 - 02:50 실행 → 05:00 만료 (2h10m)")
    fun calculateDurationByHours_roundsUpToNextHour_case2() {
        // arrange
        val now = LocalDateTime.of(2025, 9, 12, 2, 50, 0)
        val expectedTarget = LocalDateTime.of(2025, 9, 12, 5, 0, 0)
        val expected = Duration.between(now, expectedTarget)

        // act
        val actual = TimeCalculatorUtils.calculateDurationByHours(now, 2)

        // assert
        assertEquals(expected, actual)
    }

    @Test
    @DisplayName("hours=2 - 03:00 실행 → 05:00 만료 (정시 그대로)")
    fun calculateDurationByHours_exactHour() {
        // arrange
        val now = LocalDateTime.of(2025, 9, 12, 3, 0, 0)
        val expectedTarget = LocalDateTime.of(2025, 9, 12, 5, 0, 0)
        val expected = Duration.between(now, expectedTarget)

        // act
        val actual = TimeCalculatorUtils.calculateDurationByHours(now, 2)

        // assert
        assertEquals(expected, actual)
    }

    @Test
    @DisplayName("hours=2 - 23:10 실행 → 다음날 01:00 만료")
    fun calculateDurationByHours_crossesMidnight() {
        // arrange
        val now = LocalDateTime.of(2025, 9, 12, 23, 10, 0)
        val expectedTarget = LocalDateTime.of(2025, 9, 13, 1, 0, 0)
        val expected = Duration.between(now, expectedTarget)

        // act
        val actual = TimeCalculatorUtils.calculateDurationByHours(now, 2)

        // assert
        assertEquals(expected, actual)
    }

    @Test
    @DisplayName("hours=2 - 01:20 실행 → base=03:20 → 03:00 만료 (30분 이전)")
    fun calculateDurationByHours_before30min() {
        // arrange
        val now = LocalDateTime.of(2025, 9, 12, 1, 20, 0)
        val expectedTarget = LocalDateTime.of(2025, 9, 12, 3, 0, 0)
        val expected = Duration.between(now, expectedTarget)

        // act
        val actual = TimeCalculatorUtils.calculateDurationByHours(now, 2)

        // assert
        assertEquals(expected, actual)
    }

    @Test
    @DisplayName("hours=2 - 01:50 실행 → base=03:50 → 04:00 만료 (30분 이후)")
    fun calculateDurationByHours_after30min() {
        // arrange
        val now = LocalDateTime.of(2025, 9, 12, 1, 50, 0)
        val expectedTarget = LocalDateTime.of(2025, 9, 12, 4, 0, 0)
        val expected = Duration.between(now, expectedTarget)

        // act
        val actual = TimeCalculatorUtils.calculateDurationByHours(now, 2)

        // assert
        assertEquals(expected, actual)
    }
}
