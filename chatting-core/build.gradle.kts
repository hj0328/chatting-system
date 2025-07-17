dependencies {
    implementation(project(":common"))
    implementation("org.springframework.boot:spring-boot-starter-websocket")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")

    // Spring Boot Starter Test (JUnit5, Mockito, AssertJ 포함)
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    // Spring Security Test
    testImplementation("org.springframework.security:spring-security-test")

    // Spring Messaging (StompHeaderAccessor, MessageBuilder 등)
    implementation("org.springframework:spring-messaging")

    implementation("org.springframework.boot:spring-boot-starter-data-redis")
}

tasks.test {
    useJUnitPlatform()
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
