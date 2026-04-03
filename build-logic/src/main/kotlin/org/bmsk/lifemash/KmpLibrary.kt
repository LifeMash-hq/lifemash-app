package org.bmsk.lifemash

import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

@OptIn(ExperimentalKotlinGradlePluginApi::class)
internal fun Project.configureKmpLibrary() {
    configure<KotlinMultiplatformExtension> {
        val androidLib = (this as org.gradle.api.plugins.ExtensionAware)
            .extensions
            .getByName("android") as KotlinMultiplatformAndroidLibraryTarget
        androidLib.apply {
            compileSdk = AndroidSdk.COMPILE_SDK
            minSdk = AndroidSdk.MIN_SDK
            compilerOptions {
                jvmTarget.set(JvmTarget.JVM_17)
                freeCompilerArgs.add("-Xexpect-actual-classes")
            }
        }

        iosArm64()
        iosSimulatorArm64()
    }
}
