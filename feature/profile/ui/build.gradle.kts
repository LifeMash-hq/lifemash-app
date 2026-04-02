plugins {
    id("lifemash.kmp.compose")
}

kotlin {
    android {
        namespace = "org.bmsk.lifemash.profile.ui"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":feature:profile:api"))
            implementation(project(":feature:profile:domain"))
            implementation(project(":feature:calendar:api"))
            implementation(project(":feature:calendar:domain"))
            implementation(project(":shared:designsystem"))
            implementation(libs.kotlinx.immutable)
            implementation(libs.kotlinx.datetime)
        }
    }
}
