import com.android.build.api.dsl.LibraryExtension

plugins {
    id("lifemash.kmp.library")
    alias(libs.plugins.kotlin.serialization)
}

configure<LibraryExtension> {
    namespace = "org.bmsk.lifemash.data.network"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.model)
            implementation(libs.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.koin.core)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.ktor.client.logging)
            implementation(libs.ksoup)
            implementation(libs.gitlive.firebase.firestore)
        }
        androidMain.dependencies {
            implementation(libs.ktor.client.okhttp)
        }
    }
}
