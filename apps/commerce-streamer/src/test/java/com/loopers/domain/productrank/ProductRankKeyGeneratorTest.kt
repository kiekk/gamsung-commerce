package com.loopers.domain.productrank

import com.loopers.support.cache.productrank.ProductRankCacheKeyGenerator
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime

class ProductRankKeyGeneratorTest {

    @DisplayName("generate(date: LocalDateTime) 메서드는 yyyyMMddHH 형식으로 키를 생성해야 한다.")
    @Test
    fun testGenerateWithLocalDateTime() {
        // arrange
        val dateTime = LocalDateTime.of(2024, 6, 15, 14, 30)

        // act
        val actualKey = ProductRankCacheKeyGenerator.generate(dateTime)

        // assert
        val expectedKey = "ranking:all:2024061514"
        assertEquals(expectedKey, actualKey)
    }

    @DisplayName("generate(date: LocalDate) 메서드는 yyyyMMdd 형식으로 키를 생성해야 한다.")
    @Test
    fun testGenerateWithLocalDate() {
        // arrange
        val date = LocalDate.of(2024, 6, 15)

        // act
        val actualKey = ProductRankCacheKeyGenerator.generate(date)

        // assert
        val expectedKey = "ranking:all:20240615"
        assertEquals(expectedKey, actualKey)
    }
}
