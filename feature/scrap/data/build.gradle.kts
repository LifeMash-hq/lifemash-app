plugins {
    id("lifemash.android.data")
    id("com.google.devtools.ksp")
}

android {
    namespace = "org.bmsk.lifemash.scrap.data"
}

dependencies {
    implementation(projects.feature.scrap.domain)

    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
}
