plugins {
    id("lifemash.kmp.data")
}

kotlin {
    android {
        namespace = "org.bmsk.lifemash.notification.data"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.feature.notification.domain)
            implementation(projects.shared.model)
            implementation(libs.kotlinx.datetime)
        }
    }
}
