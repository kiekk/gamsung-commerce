package com.loopers.support

import com.loopers.domain.stock.StockService
import org.mockito.Mockito.mock
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary

@TestConfiguration
class StockServiceMockConfig {
    @Bean
    @Primary
    fun mockStockService(): StockService = mock()
}
