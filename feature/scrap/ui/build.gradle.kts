plugins {
    id("lifemash.kmp.compose")
}

kotlin {
    android {
        namespace = "org.bmsk.lifemash.scrap.ui"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":feature:scrap:api"))
            implementation(project(":feature:scrap:domain"))
            implementation(project(":shared:designsystem"))
            implementation(project(":model"))

            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.material.icons.extended)
            implementation(libs.compose.ui)

            implementation(libs.jetbrains.navigation.compose)
            implementation(libs.jetbrains.lifecycle.viewmodel.compose)
            implementation(libs.jetbrains.lifecycle.runtime.compose)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.kotlinx.immutable)
            implementation(libs.kotlinx.datetime)
        }
    }
}
