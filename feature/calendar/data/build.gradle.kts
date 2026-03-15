import com.android.build.api.dsl.LibraryExtension
plugins {
    id("lifemash.kmp.library")
    id("org.jetbrains.kotlin.plugin.serialization")
}

configure<com.android.build.api.dsl.LibraryExtension> {
    namespace = "org.bmsk.lifemash.calendar.data"
}

kotlin {
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
