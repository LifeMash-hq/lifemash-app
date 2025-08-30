plugins {
    id("lifemash.android.data")
    id("com.google.devtools.ksp")
}

android {
    namespace = "org.bmsk.lifemash.data.scrap"
}

dependencies {
    implementation(projects.domain.featureScrap)

    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
}
