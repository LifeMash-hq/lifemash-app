plugins {
    id("lifemash.kmp.data")
}
kotlin {
    android { namespace = "org.bmsk.lifemash.feed.data" }
    sourceSets {
        commonMain.dependencies {
            implementation(projects.feature.feed.domain)
        }
    }
}
