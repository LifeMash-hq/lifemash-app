plugins {
    id("lifemash.kmp.data")
}

kotlin {
    android {
        namespace = "org.bmsk.lifemash.profile.data"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.feature.profile.domain)
        }
    }
}
