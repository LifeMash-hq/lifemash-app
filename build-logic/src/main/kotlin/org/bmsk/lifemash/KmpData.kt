package org.bmsk.lifemash

import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

internal fun Project.configureKmpData() {
    val libs = extensions.libs
    configure<KotlinMultiplatformExtension> {
        applyDefaultHierarchyTemplate()

        sourceSets.getByName("commonMain").dependencies {
            implementation(libs.findLibrary("coroutines-core").get())
            implementation(libs.findLibrary("koin-core").get())
            implementation(libs.findLibrary("kotlinx-serialization-json").get())
            implementation(libs.findLibrary("ktor-client-core").get())
            implementation(libs.findLibrary("ktor-client-content-negotiation").get())
            implementation(libs.findLibrary("ktor-serialization-kotlinx-json").get())
        }
        sourceSets.getByName("commonTest").dependencies {
            implementation(kotlin("test"))
            implementation(libs.findLibrary("coroutines-test").get())
        }
        sourceSets.getByName("androidMain").dependencies {
            implementation(libs.findLibrary("ktor-client-okhttp").get())
        }
        sourceSets.getByName("iosMain").dependencies {
            implementation(libs.findLibrary("ktor-client-darwin").get())
        }
    }
}
