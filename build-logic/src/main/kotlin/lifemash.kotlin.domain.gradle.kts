import org.bmsk.lifemash.configureDagger
import org.bmsk.lifemash.libs

plugins {
    id("lifemash.kotlin.library")
}

configureDagger()

dependencies {
    implementation(project(":domain:core"))

    val libs = project.extensions.libs
    implementation(libs.findLibrary("inject").get())
}
