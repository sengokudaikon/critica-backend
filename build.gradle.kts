import io.github.cdimascio.dotenv.Dotenv

group = "net.critika"
version = "0.0.1"

plugins {
    kotlin("jvm") version "1.8.20"
    id("io.ktor.plugin") version "2.2.4"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.8.20"
    id("com.google.devtools.ksp") version "1.8.20-1.0.10"
    id("org.flywaydb.flyway") version "7.15.0"
    id("io.gitlab.arturbosch.detekt") version "1.19.0"
    id("jacoco")
    id("org.jlleitschuh.gradle.ktlint") version "11.3.1"
}
ktlint {
    filter {
        exclude("**/build/generated/ksp/**")
        exclude("**/generated/**")
        exclude { projectDir.toURI().relativize(it.file.toURI()).path.contains("/generated/") }
    }
}
detekt {
    buildUponDefaultConfig = true
    parallel = true
    autoCorrect = true
    config = files("$rootDir/detekt.yaml")
}

val dotenv = Dotenv.configure().ignoreIfMissing().load()
val flywayUrl = dotenv["FLYWAY_URL"]
val flywayUser = dotenv["FLYWAY_USER"]
val flywayPassword = dotenv["FLYWAY_PASSWORD"]

flyway {
    url = flywayUrl
    user = flywayUser
    password = flywayPassword
}
sourceSets.main {
    java.srcDirs("build/generated/ksp/main/kotlin")
}
kotlin {
    sourceSets.main {
        kotlin.srcDir("build/generated/ksp/main/kotlin")
    }

    sourceSets.test {
        kotlin.srcDir("build/generated/ksp/test/kotlin")
    }
}

ktor {
    fatJar {
        archiveFileName.set("critika.jar")
    }

    docker {
        jreVersion.set(io.ktor.plugin.features.JreVersion.JRE_17)
        localImageName.set("sample-docker-image")
        imageTag.set("0.0.1")
        portMappings.set(
            listOf(
                io.ktor.plugin.features.DockerPortMapping(
                    80,
                    8080,
                    io.ktor.plugin.features.DockerPortMappingProtocol.TCP
                )
            )
        )

        externalRegistry.set(
            io.ktor.plugin.features.DockerImageRegistry.dockerHub(
                appName = provider { "critica-backend" },
                username = providers.environmentVariable("DOCKER_HUB_USERNAME"),
                password = providers.environmentVariable("DOCKER_HUB_PASSWORD")
            )
        )
    }
}

application {
    mainClass.set("net.critika.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}
repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}
buildscript {
    dependencies {
        classpath("io.github.cdimascio:java-dotenv:5.2.2")
    }
}

dependencies {
    implementation("com.google.devtools.ksp:symbol-processing-api:1.8.20-1.0.10")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.8.20")
    implementation("io.ktor:ktor-server-core-jvm:2.2.4")
    implementation("io.ktor:ktor-server-resources:2.2.4")
    implementation("io.ktor:ktor-server-caching-headers-jvm:2.2.4")
    implementation("io.ktor:ktor-server-cors-jvm:2.2.4")
    implementation("io.ktor:ktor-server-openapi:2.2.4")
    implementation("io.ktor:ktor-server-swagger:2.2.4")
    implementation("io.ktor:ktor-server-partial-content-jvm:2.2.4")
    implementation("io.ktor:ktor-server-call-logging-jvm:2.2.4")
    implementation("io.ktor:ktor-server-metrics-jvm:2.2.4")
    implementation("io.ktor:ktor-server-call-id-jvm:2.2.4")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:2.2.4")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:2.2.4")
    implementation("org.jetbrains.exposed:exposed-core:0.40.1")
    implementation("org.jetbrains.exposed:exposed-dao:0.40.1")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.40.1")
    implementation("org.postgresql:postgresql:42.5.4")
    implementation("io.ktor:ktor-server-websockets-jvm:2.2.4")
    implementation("io.ktor:ktor-server-netty-jvm:2.2.4")
    implementation("ch.qos.logback:logback-classic:1.4.6")
    implementation("de.nycode:bcrypt:2.3.0")
    implementation("org.testng:testng:7.7.0")
    implementation("org.flywaydb:flyway-core:9.16.0")
    implementation("com.zaxxer:HikariCP:5.0.1")
    implementation("io.insert-koin:koin-ktor:3.3.1")
    implementation("io.insert-koin:koin-core:3.3.3")
    implementation("io.insert-koin:koin-test:3.3.3")
    implementation("io.insert-koin:koin-logger-slf4j:3.3.1")
    implementation("io.insert-koin:koin-annotations:1.1.1")
    implementation("io.arrow-kt:arrow-core:1.2.0-RC")
    implementation("io.arrow-kt:arrow-fx-coroutines:1.2.0-RC")
    implementation("org.bouncycastle:bcprov-jdk18on:1.72")
    implementation("com.github.dimitark.ktor-annotations:annotations:0.0.3")
    implementation("io.swagger.codegen.v3:swagger-codegen-generators:1.0.38")
    implementation("io.github.cdimascio:dotenv-kotlin:6.4.0")
    implementation("io.ktor:ktor-server-host-common-jvm:2.2.4")
    implementation("io.ktor:ktor-server-status-pages-jvm:2.2.4")
    implementation("net.bytebuddy:byte-buddy:1.14.2")
    implementation("com.sun.mail:jakarta.mail:2.0.1")
    implementation("io.ktor:ktor-server-jvm:2.2.4")
    implementation("io.ktor:ktor-server-auth-jwt-jvm:2.2.4")
    implementation("app.softwork:kotlinx-uuid-core:0.0.18")
    implementation("org.jetbrains.exposed:exposed-java-time:0.30.1")
    implementation("io.gitlab.arturbosch.detekt:detekt-formatting:1.22.0")
    implementation("com.pinterest.ktlint:ktlint-core:0.48.2")
    implementation("com.pinterest.ktlint:ktlint-ruleset-standard:0.48.2")

    // annotations
    annotationProcessor("org.hibernate.validator:hibernate-validator-annotation-processor:8.0.0.Final")
    ksp("io.insert-koin:koin-ksp-compiler:1.1.1")
    ksp("io.arrow-kt:arrow-optics-ksp-plugin:1.2.0-RC")
    ksp("com.github.dimitark.ktor-annotations:processor:0.0.3")

    // test
    testImplementation("org.testcontainers:testcontainers:1.17.6")
    testImplementation("org.testcontainers:postgresql:1.17.6")
    testImplementation("io.ktor:ktor-server-test-host-jvm:2.2.4")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
    testImplementation("org.mockito:mockito-core:5.2.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:4.1.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.2")
    testImplementation("io.ktor:ktor-server-tests-jvm:2.2.4")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:1.8.20-RC")
    testImplementation("com.github.npetzall.testcontainers.junit:junit-parent:1.10.1.0.0")
    testImplementation("org.testcontainers:junit-jupiter:1.17.6")
    testImplementation("com.tngtech.archunit:archunit-junit5:1.0.0")
    testImplementation("com.github.tomakehurst:wiremock-jre8:2.35.0")
    testImplementation("io.rest-assured:rest-assured:5.3.0")
    testImplementation("io.rest-assured:kotlin-extensions:5.3.0")
    testImplementation("io.qameta.allure:allure-junit5:2.21.0")
    testImplementation("io.kotest:kotest-runner-junit5:5.6.1")
    testImplementation("io.kotest:kotest-assertions-core-jvm:5.6.1")
    testImplementation("io.kotest.extensions:kotest-assertions-ktor:2.0.0")
    testImplementation("io.kotest.extensions:kotest-extensions-koin:1.1.0")
}

tasks {
    build {
        dependsOn("detekt")
    }
}

tasks.test {
    useJUnitPlatform()
//    finalizedBy("jacocoTestReport") // This ensures the test report is generated after the test task is executed
}

// tasks.jacocoTestReport {
//    reports {
//        xml.required
//        html.required
//    }
// }

jacoco {
    toolVersion = "0.8.7"
}
