plugins {
    id("lifemash.kmp.compose")
}

kotlin {
    android {
        namespace = "org.bmsk.lifemash.auth.ui"
    }

    sourceSets {
        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.coroutines.test)
        }
        commonMain.dependencies {
            implementation(project(":feature:auth:api"))
            implementation(project(":feature:auth:domain"))
            implementation(project(":shared:designsystem"))

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
        }
    }
}
