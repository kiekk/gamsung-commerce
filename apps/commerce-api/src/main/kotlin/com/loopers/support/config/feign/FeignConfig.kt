package com.loopers.support.config.feign

import feign.Feign
import feign.Request
import feign.Retryer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.TimeUnit.MILLISECONDS

@Configuration
class FeignConfig {

    @Bean
    fun feignBuilder(): Feign.Builder {
        return Feign.builder()
            .options(
                Request.Options(
                    1000, // 1초
                    MILLISECONDS,
                    3000, // 3초
                    MILLISECONDS,
                    true,
                ),
            )
            .retryer(Retryer.Default())
    }
}
