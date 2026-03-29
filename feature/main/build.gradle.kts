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

    listOf(iosArm64(), iosSimulatorArm64()).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "LifeMashShared"
            isStatic = true
        }
    }

    sourceSets {
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
        commonMain.dependencies {
            implementation(project(":shared:common"))
            implementation(project(":shared:designsystem"))
            implementation(project(":shared:model"))
            implementation(project(":shared:network"))

            implementation(project(":feature:calendar:domain"))
            implementation(project(":feature:calendar:data"))
            implementation(project(":feature:calendar:api"))
            implementation(project(":feature:calendar:ui"))
            implementation(project(":feature:auth:domain"))
            implementation(project(":feature:auth:data"))
            implementation(project(":feature:auth:api"))
            implementation(project(":feature:auth:ui"))
            implementation(project(":feature:notification:domain"))
            implementation(project(":feature:notification:data"))
            implementation(project(":feature:notification:api"))
            implementation(project(":feature:notification:ui"))
            implementation(project(":feature:profile:domain"))
            implementation(project(":feature:profile:data"))
            implementation(project(":feature:profile:api"))
            implementation(project(":feature:profile:ui"))
            implementation(project(":feature:feed:domain"))
            implementation(project(":feature:feed:data"))
            implementation(project(":feature:feed:api"))
            implementation(project(":feature:feed:ui"))
            implementation(project(":feature:explore:domain"))
            implementation(project(":feature:explore:data"))
            implementation(project(":feature:explore:api"))
            implementation(project(":feature:explore:ui"))
            implementation(project(":feature:event-detail:api"))
            implementation(project(":feature:event-detail:ui"))

            implementation(libs.compose.runtime)
            implementation(libs.compose.material.icons.extended)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)

            implementation(libs.jetbrains.navigation.compose)
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.jetbrains.lifecycle.viewmodel.compose)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.ktor.client.logging)
            implementation(libs.ktor.client.auth)
            implementation(libs.androidx.datastore.preferences)
        }
        androidMain.dependencies {
            implementation(libs.androidx.appcompat)
        }
        iosMain.dependencies {
            implementation(libs.gitlive.firebase.analytics)
        }
    }
}
