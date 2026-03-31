plugins {
    id("lifemash.kmp.data")
}

kotlin {
    android {
        namespace = "org.bmsk.lifemash.auth.data"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.feature.auth.domain)
            implementation(libs.kotlinx.datetime)
            implementation(libs.androidx.datastore.preferences)
        }
    }
}
