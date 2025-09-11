package com.loopers

import jakarta.annotation.PostConstruct
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@EnableScheduling
@SpringBootApplication
class CommerceStreamerApplication {

    @PostConstruct
    fun started() {
        // set timezone
        java.util.TimeZone.setDefault(java.util.TimeZone.getTimeZone("Asia/Seoul"))
    }
}

fun main(args: Array<String>) {
    runApplication<CommerceStreamerApplication>(*args)
}
