plugins {
    id("lifemash.kmp.library")
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    android {
        namespace = "org.bmsk.lifemash.scrap.domain"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.model)
            implementation(libs.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.koin.core)
        }
    }
}
