plugins {
    id("lifemash.kotlin.library")
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.shadow)
    application
}

tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
    config.from(file("detekt-server.yml"))
}

application {
    mainClass.set("org.bmsk.lifemash.ApplicationKt")
}

tasks.shadowJar {
    archiveFileName.set("lifemash-server.jar")
    mergeServiceFiles()
}

tasks.named<JavaExec>("run") {
    workingDir = projectDir
}

kotlin {
    compilerOptions {
        optIn.add("kotlin.uuid.ExperimentalUuidApi")
    }
}

dependencies {
    implementation(projects.core.domain)
    implementation(projects.core.infra)
    implementation(projects.shared.model)

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

    // Ktor Client (for DI HttpClient creation)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.content.negotiation)

    // Koin
    implementation(libs.koin.core)
    implementation(libs.koin.ktor)

    // Serialization
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.datetime)

    // Logging
    implementation(libs.logback.classic)

    // Test
    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.ktor.client.content.negotiation)
    testImplementation(kotlin("test"))
}
