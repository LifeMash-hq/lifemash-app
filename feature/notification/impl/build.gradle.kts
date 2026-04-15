plugins {
    id("lifemash.kmp.compose")
}

kotlin {
    android {
        namespace = "org.bmsk.lifemash.notification.impl"
        withDeviceTestBuilder {}.configure {
            instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }
        // CMP 1.10 + 통합 KMP 플러그인은 Android resources 처리가 기본 꺼져 있음.
        // compose resources(strings.xml 등)를 APK assets로 패키징하려면 명시 활성화 필요.
        androidResources.enable = true
    }

    sourceSets {
        commonTest.dependencies {
            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlinx.immutable)
        }
        commonMain.dependencies {
            implementation(project(":domain"))
            implementation(project(":feature:notification:api"))
            implementation(project(":shared:designsystem"))
            implementation(libs.kotlinx.immutable)
            implementation(libs.kotlinx.datetime)
            implementation(libs.compose.components.resources)
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

compose.resources {
    publicResClass = false
}
