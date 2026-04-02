plugins {
    id("lifemash.kmp.data")
}

kotlin {
    android {
        namespace = "org.bmsk.lifemash.moment.data"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.feature.moment.domain)
            implementation(projects.shared.model)
        }
    }
}
