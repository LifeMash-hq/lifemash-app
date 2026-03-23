plugins {
    id("lifemash.kmp.compose")
}

kotlin {
    android {
        namespace = "org.bmsk.lifemash.main"
        androidResources {
            enable = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":shared:common"))
            implementation(project(":shared:webview"))
            implementation(project(":shared:designsystem"))
            implementation(project(":shared:model"))
            implementation(project(":feature:notification:api"))
            implementation(project(":feature:notification:ui"))
            implementation(project(":feature:calendar:api"))
            implementation(project(":feature:calendar:ui"))
            implementation(project(":feature:auth:api"))
            implementation(project(":feature:auth:ui"))
            implementation(project(":feature:auth:domain"))
            implementation(project(":feature:assistant:api"))
            implementation(project(":feature:assistant:ui"))
            implementation(project(":feature:home:api"))
            implementation(project(":feature:home:ui"))

            implementation(libs.compose.runtime)
            implementation(libs.compose.material.icons.extended)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)

            implementation(libs.jetbrains.navigation.compose)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.jetbrains.lifecycle.viewmodel.compose)
        }
        androidMain.dependencies {
            implementation(libs.androidx.appcompat)
        }
        iosMain.dependencies {
            implementation(project(":shared:network"))
            implementation(project(":feature:calendar:domain"))
            implementation(project(":feature:calendar:data"))
            implementation(project(":feature:auth:domain"))
            implementation(project(":feature:auth:data"))
            implementation(project(":feature:notification:domain"))
            implementation(project(":feature:notification:data"))
            implementation(project(":feature:assistant:domain"))
            implementation(project(":feature:assistant:data"))
            implementation(project(":feature:home:api"))
            implementation(project(":feature:home:domain"))
            implementation(project(":feature:home:data"))
            implementation(project(":feature:home:ui"))
            implementation(libs.koin.core)
            implementation(libs.ktor.client.darwin)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.ktor.client.logging)
            implementation(libs.ktor.client.auth)
            implementation(libs.androidx.datastore.preferences)
        }
    }
}
