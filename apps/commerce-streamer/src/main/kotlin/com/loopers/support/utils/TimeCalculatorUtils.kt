package com.loopers.support.utils

import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime

object TimeCalculatorUtils {
    /**
     * N일 뒤 자정(00:00)까지 TTL 계산
     * 기준: 정오(12:00)
     * @param days 몇 일 뒤 자정까지 만료시킬지
     *
     * 예:
     * - 25.09.11 11:50 실행, days=2 → 25.09.13 00:00 만료
     * - 25.09.11 12:10 실행, days=2 → 25.09.14 00:00 만료 (+1일)
     * - 25.09.11 23:50 실행, days=2 → 25.09.14 00:00 만료 (+1일)
     */
    fun calculateDurationByDays(now: LocalDateTime, days: Long): Duration {
        val noon = LocalTime.NOON
        val adjustedDays = if (now.toLocalTime().isAfter(noon)) days + 1 else days

        val targetMidnight = now.toLocalDate()
            .plusDays(adjustedDays)
            .atStartOfDay()

        return Duration.between(now, targetMidnight)
    }

    /**
     * 현재 시간 기준으로 TTL을 hours 시간 뒤 "정시 시각"으로 설정
     * 30분 기준으로 반올림 처리
     *
     * @param hours 몇 시간 뒤를 기준으로 TTL을 설정할지
     *
     * 예:
     * - 01:20 실행, hours=2 → base=03:20 → 03:00 만료 (30분 이전 → 그대로)
     * - 01:50 실행, hours=2 → base=03:50 → 04:00 만료 (30분 이후 → 올림)
     * - 02:50 실행, hours=2 → base=04:50 → 05:00 만료 (30분 이후 → 올림)
     * - 02:10 실행, hours=2 → base=04:10 → 04:00 만료 (30분 이전 → 그대로)
     */
    fun calculateDurationByHours(now: LocalDateTime, hours: Long): Duration {
        val base = now.plusHours(hours)

        // 30분 기준 반올림 처리
        val rounded = if (base.minute < 30) {
            base.withMinute(0).withSecond(0).withNano(0)
        } else {
            base.withMinute(0).withSecond(0).withNano(0).plusHours(1)
        }

        return Duration.between(now, rounded)
    }
}
