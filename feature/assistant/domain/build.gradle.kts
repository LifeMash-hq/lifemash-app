plugins {
    id("lifemash.kmp.library")
    id("org.jetbrains.kotlin.plugin.serialization")
}

kotlin {
    android {
        namespace = "org.bmsk.lifemash.assistant.domain"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":shared:validation"))
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.coroutines.core)
            implementation(libs.kotlinx.datetime)
            implementation(libs.koin.core)
        }
    }
}
