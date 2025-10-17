plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    kotlin("jvm")
    kotlin("plugin.spring")
}

dependencies {
    implementation(project(":common"))
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.cloud:spring-cloud-starter-gateway")
    implementation("net.logstash.logback:logstash-logback-encoder:7.4")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "javax.servlet", module = "javax.servlet-api")
    }
    testImplementation("jakarta.servlet:jakarta.servlet-api:6.0.0")
    testImplementation("org.springframework.boot:spring-boot-starter-web")
}

kotlin {
    jvmToolchain(23)
}
