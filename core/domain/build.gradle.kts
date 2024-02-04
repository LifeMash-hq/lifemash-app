plugins {
    id("lifemash.coroutine.library")
    id("kotlinx-serialization")
}

dependencies {
    implementation(projects.core.model)
    implementation(libs.inject)
}
