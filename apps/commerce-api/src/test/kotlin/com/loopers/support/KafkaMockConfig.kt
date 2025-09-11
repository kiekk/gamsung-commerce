package com.loopers.support

import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.SendResult
import java.util.concurrent.CompletableFuture

@TestConfiguration
class KafkaMockConfig {
    @Bean
    @Primary
    fun kafkaTemplateMock(): KafkaTemplate<Any, Any> {
        val mockTemplate = mock<KafkaTemplate<Any, Any>>()
        val future = CompletableFuture.completedFuture(mock<SendResult<Any, Any>>())
        whenever(mockTemplate.send(any<String>(), any(), any())).thenReturn(future)
        return mockTemplate
    }
}
