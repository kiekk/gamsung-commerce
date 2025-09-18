package com.loopers.batch.job.productmetrics.writer

import com.loopers.batch.writer.productmetrics.WeeklyProductMetricsWriter
import com.loopers.domain.productmetrics.ProductMetricsWeekly
import com.loopers.domain.productmetrics.ProductMetricsWeeklyRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.kotlin.check
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.springframework.batch.item.Chunk

class WeeklyProductMetricsWriterTest {
    @Test
    @DisplayName("WeeklyProductMetricsWriter는 전달받은 ProductMetricsWeekly 목록을 저장한다.")
    fun shouldSaveAllWeeklyItems() {
        // arrange
        val repo = mock<ProductMetricsWeeklyRepository>()
        val writer =
            WeeklyProductMetricsWriter(repo)
        val item1 = mock<ProductMetricsWeekly>()
        val item2 = mock<ProductMetricsWeekly>()
        val chunk: Chunk<ProductMetricsWeekly> = Chunk.of(item1, item2)

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
