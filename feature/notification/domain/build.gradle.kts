import com.android.build.api.dsl.LibraryExtension
plugins {
    id("lifemash.kmp.library")
}

configure<com.android.build.api.dsl.LibraryExtension> {
    namespace = "org.bmsk.lifemash.notification.domain"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.coroutines.core)
            implementation(libs.kotlinx.datetime)
            implementation(libs.koin.core)
        }
    }
}
