import org.bmsk.lifemash.configureHiltKotlin
import org.bmsk.lifemash.libs

plugins {
    id("lifemash.kotlin.library")
}

configureHiltKotlin()

dependencies {
    implementation(project(":model"))

    val libs = project.extensions.libs
    implementation(libs.findLibrary("inject").get())
}
