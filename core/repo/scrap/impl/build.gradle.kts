plugins {
    id("lifemash.android.library")
    id("lifemash.android.hilt")
    id("kotlinx-serialization")
    alias(libs.plugins.ksp)
}

android {
    namespace = "org.bmsk.lifemash.core.repo.scrap"
}

dependencies {
    implementation(projects.core.repo.scrap.api)
    implementation(projects.core.model)

    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
}
