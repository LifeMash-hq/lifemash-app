plugins {
    id("lifemash.android.library")
    id("lifemash.android.hilt")
    id("kotlinx-serialization")
}

android {
    namespace = "org.bmsk.lifemash.core.repo.scrap"
}

dependencies {
    implementation(projects.core.repo.scrap.api)
}