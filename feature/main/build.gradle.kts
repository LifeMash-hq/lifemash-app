import java.util.Properties

plugins {
    id("lifemash.kmp.compose")
    alias(libs.plugins.kotlin.serialization)
}

val localProps = Properties().apply {
    rootProject.file("local.properties").takeIf { it.exists() }?.inputStream()?.use { load(it) }
}
val iosDebugBackendUrl: String = localProps.getProperty("BACKEND_BASE_URL")
    ?: "https://lifemash-backend.onrender.com"

val generateIosBackendConfig by tasks.registering {
    val outputDir = layout.buildDirectory.dir("generated/iosMain/kotlin")
    outputs.dir(outputDir)
    doLast {
        val f = outputDir.get().asFile
            .resolve("org/bmsk/lifemash/main/BackendConfig.kt")
        f.parentFile.mkdirs()
        f.writeText(
            """
            package org.bmsk.lifemash.main

            internal const val IOS_DEBUG_BACKEND_URL = "$iosDebugBackendUrl"
            """.trimIndent()
        )
    }
}

kotlin {
    android {
        namespace = "org.bmsk.lifemash.main"
        androidResources {
            enable = true
        }
    }

    listOf(iosArm64(), iosSimulatorArm64()).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "LifeMashShared"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":shared:common"))
            implementation(project(":shared:designsystem"))
            implementation(project(":shared:model"))
            implementation(project(":shared:network"))

            implementation(project(":feature:calendar:domain"))
            implementation(project(":feature:calendar:data"))
            implementation(project(":feature:calendar:api"))
            implementation(project(":feature:calendar:ui"))
            implementation(project(":feature:auth:domain"))
            implementation(project(":feature:auth:data"))
            implementation(project(":feature:auth:api"))
            implementation(project(":feature:auth:ui"))
            implementation(project(":feature:notification:domain"))
            implementation(project(":feature:notification:data"))
            implementation(project(":feature:notification:api"))
            implementation(project(":feature:notification:ui"))
            implementation(project(":feature:profile:domain"))
            implementation(project(":feature:profile:data"))
            implementation(project(":feature:profile:api"))
            implementation(project(":feature:profile:ui"))
            implementation(project(":feature:feed:domain"))
            implementation(project(":feature:feed:data"))
            implementation(project(":feature:feed:api"))
            implementation(project(":feature:feed:ui"))
            implementation(project(":feature:event-detail:api"))
            implementation(project(":feature:event-detail:domain"))
            implementation(project(":feature:event-detail:data"))
            implementation(project(":feature:event-detail:ui"))
            implementation(project(":feature:memo:api"))
            implementation(project(":feature:memo:domain"))
            implementation(project(":feature:memo:data"))
            implementation(project(":feature:memo:ui"))
            implementation(project(":feature:moment:api"))
            implementation(project(":feature:moment:domain"))
            implementation(project(":feature:moment:data"))
            implementation(project(":feature:moment:ui"))
            implementation(project(":feature:onboarding:api"))
            implementation(project(":feature:onboarding:domain"))
            implementation(project(":feature:onboarding:data"))
            implementation(project(":feature:onboarding:ui"))

            implementation(libs.koin.core)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.ktor.client.logging)
            implementation(libs.ktor.client.auth)
            implementation(libs.androidx.datastore.preferences)
        }
        androidMain.dependencies {
            implementation(libs.androidx.appcompat)
        }
        iosMain {
            kotlin.srcDir(generateIosBackendConfig.map { it.outputs.files.first() })
            dependencies {
                implementation(libs.gitlive.firebase.analytics)
            }
        }
    }
}
