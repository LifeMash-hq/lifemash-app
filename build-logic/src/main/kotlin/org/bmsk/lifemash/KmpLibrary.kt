package org.bmsk.lifemash

import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework

@OptIn(ExperimentalKotlinGradlePluginApi::class)
internal fun Project.configureKmpLibrary() {
    configure<KotlinMultiplatformExtension> {
        androidTarget {
            compilerOptions {
                jvmTarget.set(JvmTarget.JVM_17)
                freeCompilerArgs.add("-Xexpect-actual-classes")
            }
        }

        val xcf = XCFramework("LifeMashShared")
        listOf(iosArm64(), iosSimulatorArm64()).forEach {
            it.binaries.framework {
                baseName = "LifeMashShared"
                isStatic = true
                xcf.add(this)
            }
        }

        // 의존성은 각 모듈의 build.gradle.kts에서 직접 구성
    }

    configure<LibraryExtension> {
        compileSdk = 36
        defaultConfig { minSdk = 28 }
        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
        }
    }
}
