plugins {
    id("lifemash.kmp.data")
}

kotlin {
    android {
        namespace = "org.bmsk.lifemash.data.network"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.shared.model)
            implementation(libs.ktor.client.logging)
            implementation(libs.ktor.client.auth)
            implementation(libs.ksoup)
            implementation(libs.gitlive.firebase.firestore)
        }
        androidMain.dependencies {
            implementation(project.dependencies.platform(libs.firebase.bom))
        }
    }
}
