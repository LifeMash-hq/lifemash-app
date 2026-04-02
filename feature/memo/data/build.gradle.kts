plugins {
    id("lifemash.kmp.data")
}

kotlin {
    android {
        namespace = "org.bmsk.lifemash.memo.data"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.feature.memo.domain)
        }
    }
}
