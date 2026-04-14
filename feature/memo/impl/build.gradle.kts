plugins {
    id("lifemash.kmp.compose")
}

kotlin {
    android {
        namespace = "org.bmsk.lifemash.memo.impl"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":domain"))
            implementation(project(":feature:memo:api"))
            implementation(project(":shared:designsystem"))
            implementation(libs.kotlinx.immutable)
        }
    }
}
