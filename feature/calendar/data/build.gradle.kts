plugins {
    id("lifemash.kmp.library")
    id("org.jetbrains.kotlin.plugin.serialization")
}

kotlin {
    android {
        namespace = "org.bmsk.lifemash.calendar.data"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.feature.calendar.domain)
            implementation(libs.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.datetime)
            implementation(libs.koin.core)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
        }
        androidMain.dependencies {
            implementation(libs.ktor.client.okhttp)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
    }
}
