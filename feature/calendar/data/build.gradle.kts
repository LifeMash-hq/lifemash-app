plugins {
    id("lifemash.kmp.data")
}

kotlin {
    android {
        namespace = "org.bmsk.lifemash.calendar.data"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.feature.calendar.domain)
            implementation(libs.kotlinx.datetime)
        }
    }
}
