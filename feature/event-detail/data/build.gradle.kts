plugins {
    id("lifemash.kmp.data")
}

kotlin {
    android {
        namespace = "org.bmsk.lifemash.eventdetail.data"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.feature.eventDetail.domain)
        }
    }
}
