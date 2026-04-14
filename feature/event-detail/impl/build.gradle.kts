plugins {
    id("lifemash.kmp.compose")
}
kotlin {
    android { namespace = "org.bmsk.lifemash.eventdetail.impl" }
    sourceSets {
        commonMain.dependencies {
            implementation(project(":domain"))
            implementation(project(":feature:event-detail:api"))
            implementation(project(":shared:platform"))
            implementation(project(":shared:designsystem"))
            implementation(libs.kotlinx.immutable)
            implementation(libs.kotlinx.datetime)
        }
    }
}
