package com.loopers.support.cache

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.kotlinModule
import org.slf4j.LoggerFactory

object DataSerializer {
    private val log = LoggerFactory.getLogger(DataSerializer::class.java)
    private val objectMapper = initialize()

    private fun initialize(): ObjectMapper {
        return ObjectMapper()
            .registerModule(JavaTimeModule())
            .registerModule(kotlinModule())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }

    fun <T> deserialize(data: String?, clazz: Class<T>): T? {
        try {
            return objectMapper.readValue(data, clazz)
        } catch (e: JsonProcessingException) {
            log.error("[DataSerializer.deserialize] data={}, clazz={}", data, clazz, e)
            return null
        }
    }

    fun <T> deserialize(data: Any?, clazz: Class<T>): T? {
        return objectMapper.convertValue(data, clazz)
    }

    fun <T> serialize(data: T): String? {
        try {
            return objectMapper.writeValueAsString(data)
        } catch (e: JsonProcessingException) {
            log.error("[DataSerializer.serialize] object={}", data, e)
            return null
        }
    }
}
