plugins {
    kotlin("jvm")
}

dependencies {
    implementation(platform("org.springframework.boot:spring-boot-dependencies:3.3.4"))
}

kotlin {
    jvmToolchain(21)
}
