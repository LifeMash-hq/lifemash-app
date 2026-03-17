plugins {
    id("lifemash.kmp.library")
}

kotlin {
    android {
        namespace = "org.bmsk.lifemash.model"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.datetime)
        }
    }
}
