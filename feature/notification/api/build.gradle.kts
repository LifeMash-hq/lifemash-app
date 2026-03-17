plugins {
    id("lifemash.kmp.compose")
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    android {
        namespace = "org.bmsk.lifemash.notification.api"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.jetbrains.navigation.compose)
            implementation(libs.kotlinx.serialization.json)
        }
    }
}
