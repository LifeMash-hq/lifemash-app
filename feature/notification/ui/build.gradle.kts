plugins {
    id("lifemash.kmp.compose")
}

// CMP 1.10.2 + 통합 KMP 플러그인: androidDeviceTest용 Compose 리소스 복사 태스크 outputDirectory 미설정 workaround
afterEvaluate {
    tasks.findByName("copyAndroidDeviceTestComposeResourcesToAndroidAssets")?.let { task ->
        val prop = task.javaClass.methods.find { it.name == "getOutputDirectory" }
            ?.invoke(task) as? org.gradle.api.file.DirectoryProperty
        if (prop != null && !prop.isPresent) {
            prop.set(layout.buildDirectory.dir("intermediates/compose_resources_assets/androidDeviceTest"))
        }
    }
}

kotlin {
    android {
        namespace = "org.bmsk.lifemash.notification.ui"
        withDeviceTestBuilder {}.configure {
            instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }
    }

    sourceSets {
        commonTest.dependencies {
            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlinx.immutable)
        }
        commonMain.dependencies {
            implementation(project(":feature:notification:api"))
            implementation(project(":feature:notification:domain"))
            implementation(project(":shared:designsystem"))
            implementation(libs.kotlinx.immutable)
            implementation(libs.kotlinx.datetime)
        }
        val androidDeviceTest by getting {
            dependencies {
                implementation(libs.androidx.compose.ui.test)
                implementation(libs.androidx.compose.ui.testManifest)
                implementation(libs.kotlinx.immutable)
                implementation(libs.kotlinx.datetime)
            }
        }
    }
}
