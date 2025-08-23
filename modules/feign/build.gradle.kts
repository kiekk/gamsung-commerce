dependencies {
    api("org.springframework.cloud:spring-cloud-starter-openfeign")
    api("org.springframework.cloud:spring-cloud-starter-circuitbreaker-resilience4j")

    testImplementation("org.springframework.cloud:spring-cloud-starter-openfeign")
    testImplementation("org.springframework.cloud:spring-cloud-starter-circuitbreaker-resilience4j")
}
