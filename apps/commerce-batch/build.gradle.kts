plugins {
    id("org.jetbrains.kotlin.plugin.jpa")
}

dependencies {
    // add-ons
    implementation(project(":modules:jpa"))
    implementation(project(":modules:redis"))
    implementation(project(":supports:jackson"))
    implementation(project(":supports:logging"))
    implementation(project(":supports:monitoring"))

    // web
    implementation("org.springframework.boot:spring-boot-starter-web")

    // querydsl
    kapt("com.querydsl:querydsl-apt::jakarta")

    // batch
    implementation("org.springframework.boot:spring-boot-starter-batch")

    // batch test
    testImplementation("org.springframework.batch:spring-batch-test")

    // test-fixtures
    testImplementation(testFixtures(project(":modules:jpa")))
    testImplementation(testFixtures(project(":modules:redis")))
}
