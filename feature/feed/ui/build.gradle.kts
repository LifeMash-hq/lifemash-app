plugins {
    id("lifemash.kmp.compose")
}

android {
    namespace = "org.bmsk.lifemash.feed.ui"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":feature:feed:domain"))
            implementation(project(":feature:feed:api"))
            implementation(project(":feature:scrap:domain"))
            implementation(project(":shared:designsystem"))
            implementation(project(":model"))

            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(compose.ui)

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
    }
}
