plugins {
    id("lifemash.android.library")
    id("lifemash.android.hilt")
    id("kotlinx-serialization")
    id("com.google.devtools.ksp")
}

android {
    namespace = "org.bmsk.lifemash.data.scrap"
}

dependencies {
    implementation(projects.data.core)
    implementation(projects.domain.featureScrap)
    implementation(projects.domain.core) // For ArticleId

    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
}
