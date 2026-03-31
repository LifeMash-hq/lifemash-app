plugins {
    id("lifemash.kmp.compose")
    alias(libs.plugins.kotlin.serialization)
}
kotlin {
    android { namespace = "org.bmsk.lifemash.feed.api" }
    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.serialization.json)
        }
    }
}
