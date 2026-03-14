plugins {
    id("lifemash.kotlin.domain")
    id("kotlinx-serialization")
}

dependencies {
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.coroutines.core)
}
