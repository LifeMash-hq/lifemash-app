plugins {
    id("lifemash.kmp.library")
}

kotlin {
    android {
        namespace = "org.bmsk.lifemash.notification.domain"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.coroutines.core)
            implementation(libs.kotlinx.datetime)
            implementation(libs.koin.core)
        }
    }
}
