import org.bmsk.lifemash.libs

plugins {
    id("lifemash.kotlin.library")
}

dependencies {
    val libs = project.extensions.libs
    implementation(libs.findLibrary("inject").get())
}

// Domain layer specific configurations can be added here later.
// For example, adding a dependency on a core domain model module if one existed.
