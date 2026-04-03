plugins {
    id("lifemash.kmp.data")
}

kotlin {
    android {
        namespace = "org.bmsk.lifemash.onboarding.data"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.feature.onboarding.domain)
        }
    }
}
