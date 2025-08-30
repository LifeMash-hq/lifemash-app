import org.bmsk.lifemash.libs

plugins {
    id("lifemash.kotlin.library")
}

dependencies {
    implementation(project(":domain:core"))

    val libs = project.extensions.libs
    implementation(libs.findLibrary("inject").get())
}
