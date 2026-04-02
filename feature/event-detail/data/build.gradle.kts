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
            implementation(projects.shared.model)
            implementation(libs.ktor.client.core)
            implementation(libs.koin.core)
        }
    }
}
