package org.bmsk.lifemash

import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

internal fun Project.configureKmpCompose() {
    val libs = extensions.libs
    configure<KotlinMultiplatformExtension> {
        sourceSets.getByName("commonMain").dependencies {
            implementation(libs.findLibrary("compose-runtime").get())
            implementation(libs.findLibrary("compose-foundation").get())
            implementation(libs.findLibrary("compose-material3").get())
            implementation(libs.findLibrary("compose-material-icons-extended").get())
            implementation(libs.findLibrary("compose-ui").get())
            implementation(libs.findLibrary("jetbrains-navigation-compose").get())
            implementation(libs.findLibrary("jetbrains-lifecycle-viewmodel-compose").get())
            implementation(libs.findLibrary("jetbrains-lifecycle-runtime-compose").get())
            implementation(libs.findLibrary("koin-compose").get())
            implementation(libs.findLibrary("koin-compose-viewmodel").get())
        }
        sourceSets.getByName("androidMain").dependencies {
            implementation(libs.findLibrary("androidx.compose.ui.tooling.preview").get())
            implementation(libs.findLibrary("androidx.compose.ui.tooling").get())
        }
        sourceSets.getByName("commonTest").dependencies {
            implementation(kotlin("test"))
            implementation(libs.findLibrary("coroutines-test").get())
        }
    }
}
