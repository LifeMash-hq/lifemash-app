import com.android.build.api.dsl.LibraryExtension
plugins {
    id("lifemash.kmp.library")
    id("org.jetbrains.kotlin.plugin.serialization")
}

configure<com.android.build.api.dsl.LibraryExtension> {
    namespace = "org.bmsk.lifemash.auth.domain"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.datetime)
            implementation(libs.koin.core)
        }
    }
}
