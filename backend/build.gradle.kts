import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension

plugins {
    id("org.springframework.boot") version "3.3.13" apply false
    id("io.spring.dependency-management") version "1.1.7" apply false
    kotlin("jvm") version "2.0.21" apply false
    kotlin("plugin.spring") version "2.0.21" apply false
    kotlin("plugin.jpa") version "2.0.21" apply false
}

extra["springCloudVersion"] = "2023.0.3"

subprojects {
    repositories {
        mavenCentral()
    }

    plugins.withId("io.spring.dependency-management") {
        configure<DependencyManagementExtension> {
            imports {
                mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
            }
        }
    }
}
