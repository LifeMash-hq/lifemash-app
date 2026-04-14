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
            implementation(project(":shared:platform"))
            implementation(project(":shared:designsystem"))

            implementation(project(":domain"))

            implementation(project(":data:core"))
            implementation(project(":data:remote"))
            implementation(project(":data:local"))

            implementation(project(":feature:auth:api"))
            implementation(project(":feature:auth:impl"))
            implementation(project(":feature:calendar:api"))
            implementation(project(":feature:calendar:impl"))
            implementation(project(":feature:notification:api"))
            implementation(project(":feature:notification:impl"))
            implementation(project(":feature:profile:api"))
            implementation(project(":feature:profile:impl"))
            implementation(project(":feature:feed:api"))
            implementation(project(":feature:feed:impl"))
            implementation(project(":feature:event-detail:api"))
            implementation(project(":feature:event-detail:impl"))
            implementation(project(":feature:memo:api"))
            implementation(project(":feature:memo:impl"))
            implementation(project(":feature:moment:api"))
            implementation(project(":feature:moment:impl"))
            implementation(project(":feature:onboarding:api"))
            implementation(project(":feature:onboarding:impl"))

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
