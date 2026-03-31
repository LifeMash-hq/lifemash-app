plugins {
    id("lifemash.kotlin.library")
    alias(libs.plugins.kotlin.serialization)
}

tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
    baseline = file("detekt-baseline.xml")
    config.from(file("detekt-server.yml"))
}
tasks.withType<io.gitlab.arturbosch.detekt.DetektCreateBaselineTask>().configureEach {
    baseline = file("detekt-baseline.xml")
    config.from(file("detekt-server.yml"))
}

kotlin {
    compilerOptions {
        optIn.add("kotlin.uuid.ExperimentalUuidApi")
    }
}

dependencies {
    implementation(projects.core.domain)
    implementation(projects.shared.model)
    implementation(projects.shared.validation)

    // Exposed ORM
    implementation(libs.exposed.core)
    implementation(libs.exposed.dao)
    implementation(libs.exposed.jdbc)
    implementation(libs.exposed.kotlin.datetime)

    // Database
    implementation(libs.postgresql)
    implementation(libs.hikaricp)

    // Ktor Client (OAuth, Claude API)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)

    // JWT
    implementation(libs.java.jwt)

    // Firebase Admin
    implementation(libs.firebase.admin.sdk)

    // Serialization
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.datetime)

    // Logging
    implementation(libs.logback.classic)

    // Test
    testImplementation(kotlin("test"))
    testImplementation(libs.coroutines.test)
}

tasks.test {
    useJUnitPlatform()
    environment("API_KEY_ENCRYPTION_SECRET", "AAECAwQFBgcICQoLDA0ODxAREhMUFRYXGBkaGxwdHh8=")
}
