plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    application
}

group = "org.bmsk.lifemash"
version = "0.0.1"

application {
    mainClass.set("org.bmsk.lifemash.ApplicationKt")
}

dependencies {
    // Backend API (service/repository interfaces)
    implementation(project(":backend:api"))

    // Backend Stub (데모 구현체, core 없을 때 fallback)
    implementation(project(":backend:stub"))

    // Shared model (공통 DTO)
    implementation(project(":shared:model"))

    // Ktor Server
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.server.cors)
    implementation(libs.ktor.server.status.pages)
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.auth.jwt)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.server.resources)

    // Ktor Client (for OAuth verification)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.content.negotiation)

    // Exposed ORM
    implementation(libs.exposed.core)
    implementation(libs.exposed.dao)
    implementation(libs.exposed.jdbc)
    implementation(libs.exposed.kotlin.datetime)

    // Database
    implementation(libs.postgresql)
    implementation(libs.hikaricp)

    // Koin DI
    implementation(libs.koin.core)
    implementation(libs.koin.ktor)

    // Firebase Admin SDK
    implementation(libs.firebase.admin)

    // Serialization
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.datetime)

    // Logging
    implementation(libs.logback.classic)

    // Test
    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.ktor.client.content.negotiation)
    testImplementation(kotlin("test"))
    testImplementation(libs.coroutines.test)
}

tasks.test {
    useJUnitPlatform()
    environment("API_KEY_ENCRYPTION_SECRET", "AAECAwQFBgcICQoLDA0ODxAREhMUFRYXGBkaGxwdHh8=")
}
