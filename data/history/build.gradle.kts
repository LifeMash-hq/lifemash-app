plugins {
    id("lifemash.android.data")
    id("com.google.devtools.ksp")
}

android {
    namespace = "org.bmsk.lifemash.data.history"
}

dependencies {
    implementation(projects.domain.featureHistory)

    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
}
