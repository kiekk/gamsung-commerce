package com.loopers.infrastructure.dataflatform

import com.loopers.domain.dataflatform.DataFlatformRepository
import com.loopers.event.payload.EventPayload
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Repository
import java.util.concurrent.TimeUnit

@Repository
class DataFlatformRepositoryImpl : DataFlatformRepository {

    private val log = LoggerFactory.getLogger(DataFlatformRepositoryImpl::class.java)

    override fun send(event: EventPayload) {
        log.info("DataFlatform send event: $event")
        TimeUnit.SECONDS.sleep(1)
    }
}
