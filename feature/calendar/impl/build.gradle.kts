plugins {
    id("lifemash.kmp.compose")
}

kotlin {
    android {
        namespace = "org.bmsk.lifemash.calendar.impl"
    }

    sourceSets {
        commonTest.dependencies {
            implementation(libs.kotlinx.datetime)
        }
        commonMain.dependencies {
            implementation(project(":domain"))
            implementation(project(":feature:calendar:api"))
            implementation(project(":shared:designsystem"))
            implementation(libs.kotlinx.immutable)
            implementation(libs.kotlinx.datetime)
        }
    }
}
