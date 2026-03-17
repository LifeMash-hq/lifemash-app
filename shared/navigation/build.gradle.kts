plugins {
    id("lifemash.kmp.compose")
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    android {
        namespace = "org.bmsk.lifemash.feature.shared.navigation"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            api(libs.jetbrains.navigation.compose)
            implementation(libs.kotlinx.serialization.json)
        }
    }
}
