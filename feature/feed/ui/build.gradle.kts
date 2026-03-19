plugins {
    id("lifemash.kmp.compose")
}

kotlin {
    android {
        namespace = "org.bmsk.lifemash.feed.ui"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":feature:feed:domain"))
            implementation(project(":feature:feed:api"))
            implementation(project(":feature:scrap:domain"))
            implementation(project(":shared:designsystem"))
            implementation(project(":shared:model"))

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
            implementation(libs.coil.compose)
            implementation(libs.coil.network.ktor3)
            implementation(libs.kotlinx.immutable)
            implementation(libs.kotlinx.datetime)
        }
        androidMain.dependencies {
            implementation(libs.androidx.compose.ui.tooling.preview)
            implementation(libs.androidx.compose.ui.tooling)
        }
    }
}
