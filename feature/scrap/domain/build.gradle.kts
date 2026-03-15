import com.android.build.api.dsl.LibraryExtension

plugins {
    id("lifemash.kmp.library")
    alias(libs.plugins.kotlin.serialization)
}

configure<LibraryExtension> {
    namespace = "org.bmsk.lifemash.scrap.domain"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.model)
            implementation(libs.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.koin.core)
        }
    }
}
