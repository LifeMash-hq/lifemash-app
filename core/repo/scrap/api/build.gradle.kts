plugins {
    id("lifemash.kotlin.library")
    id("kotlinx-serialization")
}

dependencies {
    implementation(projects.core.model)
    implementation(libs.kotlinx.serialization.json)
}
