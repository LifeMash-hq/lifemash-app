plugins {
    id("lifemash.kmp.compose")
}

kotlin {
    android {
        namespace = "org.bmsk.lifemash.memo.ui"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":feature:memo:api"))
            implementation(project(":feature:memo:domain"))
            implementation(project(":feature:calendar:domain"))
            implementation(project(":shared:designsystem"))
            implementation(libs.kotlinx.immutable)
        }
    }
}
