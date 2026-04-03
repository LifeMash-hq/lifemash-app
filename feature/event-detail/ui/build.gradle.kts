plugins {
    id("lifemash.kmp.compose")
}
kotlin {
    android { namespace = "org.bmsk.lifemash.eventdetail.ui" }
    sourceSets {
        commonMain.dependencies {
            implementation(project(":feature:event-detail:api"))
            implementation(project(":feature:event-detail:domain"))
            implementation(project(":shared:common"))
            implementation(project(":shared:designsystem"))
            implementation(libs.kotlinx.immutable)
            implementation(libs.kotlinx.datetime)
        }
    }
}
