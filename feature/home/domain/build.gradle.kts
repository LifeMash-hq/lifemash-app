plugins {
    id("lifemash.kmp.library")
}

kotlin {
    android {
        namespace = "org.bmsk.lifemash.home.domain"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.feature.home.api)
            implementation(libs.coroutines.core)
            implementation(libs.koin.core)
        }
    }
}
