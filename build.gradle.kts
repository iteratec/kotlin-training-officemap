import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("org.springframework.boot:spring-boot-gradle-plugin:${Versions.springBoot}")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}")
        classpath("org.jetbrains.kotlin:kotlin-allopen:${Versions.kotlin}")
        classpath("org.jetbrains.kotlin:kotlin-noarg:${Versions.kotlin}")
        classpath("org.owasp:dependency-check-gradle:4.0.2")
    }
}

plugins {
    java
    kotlin("jvm") version Versions.kotlin
    kotlin("plugin.spring") version Versions.kotlin
    kotlin("plugin.allopen") version Versions.kotlin
    kotlin("plugin.jpa") version Versions.kotlin
    id("org.springframework.boot") version Versions.springBoot
    id("io.spring.dependency-management") version "1.0.6.RELEASE"
    id("org.owasp.dependencycheck") version "4.0.2"
}

repositories {
    mavenCentral()
}

dependencies {
    compile("org.springframework.boot:spring-boot-starter-web") {
        exclude(module = "spring-boot-starter-tomcat")
    }
    compile("org.springframework.boot:spring-boot-starter-jetty")
    compile("org.springframework.boot:spring-boot-starter-data-jpa")
    compile("org.springframework.boot:spring-boot-starter-security")
    compile("org.springframework.boot:spring-boot-starter-actuator")
    compile("org.springframework.boot:spring-boot-devtools")
    compile("org.springframework:spring-tx")
    compile("io.springfox:springfox-swagger2:${Versions.swagger}")
    compile("io.springfox:springfox-swagger-ui:${Versions.swagger}")
    compile("com.google.code.gson:gson:${Versions.gson}")
    compile("org.postgresql:postgresql:42.2.5")
    compile("com.microsoft.azure:azure-active-directory-spring-boot-starter:2.0.3")
    compile("org.springframework.security:spring-security-oauth2-client:5.0.7.RELEASE")
    compile("org.springframework.security:spring-security-oauth2-jose:5.0.7.RELEASE")
    compile("org.flywaydb:flyway-core:${Versions.flyway}")
    compile("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Versions.kotlin}")
    compile("com.fasterxml.jackson.module:jackson-module-kotlin:2.9.8")

    compile("javax.xml.ws:jaxws-api:2.3.1")
    compile("org.javassist:javassist:3.25.0-GA")
    compile("javax.activation:activation:1.1.1")

    runtime("com.h2database:h2")

    testImplementation("org.junit.jupiter:junit-jupiter-api:${Versions.junit}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${Versions.junit}")
    testCompile("org.hamcrest:hamcrest-core:2.1")
    testCompile("org.springframework.boot:spring-boot-starter-test") {
        exclude(module = "junit")
    }
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }

    jar {
        archiveBaseName.set("office-map")
        archiveVersion.set("0.1.0")
    }

    test {
        useJUnitPlatform()
    }
}

allOpen {
    annotations(
            "javax.persistence.Entity",
            "javax.persistence.MappedSuperclass",
            "javax.persistence.Embeddable"
    )
}
