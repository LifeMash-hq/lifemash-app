plugins {
    id("lifemash.kmp.compose")
}

kotlin {
    android {
        namespace = "org.bmsk.lifemash.calendar.ui"
    }

    sourceSets {
        commonTest.dependencies {
            implementation(libs.kotlinx.datetime)
        }
        commonMain.dependencies {
            implementation(project(":feature:calendar:api"))
            implementation(project(":feature:calendar:domain"))
            implementation(project(":feature:auth:domain"))
            implementation(project(":shared:designsystem"))
            implementation(libs.kotlinx.immutable)
            implementation(libs.kotlinx.datetime)
        }
    }
}
