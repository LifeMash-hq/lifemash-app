plugins {
    id("lifemash.kmp.compose")
}

kotlin {
    android {
        namespace = "org.bmsk.lifemash.moment.ui"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":feature:moment:api"))
            implementation(project(":feature:moment:domain"))
            implementation(project(":shared:designsystem"))
            implementation(libs.kotlinx.immutable)
        }
    }
}
