plugins {
    id("lifemash.kotlin.domain")
}

dependencies {
    implementation(projects.core.model)
    implementation(projects.domain.core)
}
