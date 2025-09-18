package com.loopers.batch.job.productmetrics.writer

import com.loopers.batch.writer.productmetrics.MonthlyProductMetricsWriter
import com.loopers.domain.productmetrics.ProductMetricsMonthly
import com.loopers.domain.productmetrics.ProductMetricsMonthlyRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.kotlin.check
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.springframework.batch.item.Chunk

class MonthlyProductMetricsWriterTest {
    @Test
    @DisplayName("MonthlyProductMetricsWriter는 전달받은 ProductMetricsMonthly 목록을 저장한다.")
    fun shouldSaveAllMonthlyItems() {
        // arrange
        val repo = mock<ProductMetricsMonthlyRepository>()
        val writer = MonthlyProductMetricsWriter(repo)
        val item1 = mock<ProductMetricsMonthly>()
        val item2 = mock<ProductMetricsMonthly>()
        val chunk: Chunk<ProductMetricsMonthly> = Chunk.of(item1, item2)

        // act
        writer.write(chunk)

        // assert
        verify(repo, times(1)).saveAll(
            check {
                assertThat(it).containsExactly(item1, item2)
            },
        )
        verifyNoMoreInteractions(repo)
    }
}
