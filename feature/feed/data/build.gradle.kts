plugins {
    id("lifemash.android.data")
    id("com.google.devtools.ksp")
}

android {
    namespace = "org.bmsk.lifemash.feed.data"
}

dependencies {
    implementation(projects.shared.network)
    implementation(projects.feature.feed.domain)

    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
}
