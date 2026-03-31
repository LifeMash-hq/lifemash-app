plugins {
    id("lifemash.kmp.compose")
}
kotlin {
    android { namespace = "org.bmsk.lifemash.feed.ui" }
    sourceSets {
        commonMain.dependencies {
            implementation(project(":feature:feed:api"))
            implementation(project(":feature:feed:domain"))
            implementation(project(":shared:designsystem"))
            implementation(libs.kotlinx.immutable)
        }
    }
}
