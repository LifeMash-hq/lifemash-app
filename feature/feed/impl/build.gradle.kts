plugins {
    id("lifemash.kmp.compose")
}
kotlin {
    android { namespace = "org.bmsk.lifemash.feed.impl" }
    sourceSets {
        commonMain.dependencies {
            implementation(project(":domain"))
            implementation(project(":feature:feed:api"))
            implementation(project(":shared:designsystem"))

            implementation(libs.kotlinx.immutable)
        }
    }
}
