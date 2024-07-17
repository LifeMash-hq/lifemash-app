plugins {
    id("lifemash.android.feature")
}

android {
    namespace = "org.bmsk.lifemash.feature.all"
}

dependencies {
    implementation(projects.core.model)
    implementation(projects.core.commonUi)
}
