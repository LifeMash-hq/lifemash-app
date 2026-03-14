import org.bmsk.lifemash.configureHiltAndroid

plugins {
    id("lifemash.android.library")
}

configureHiltAndroid()

dependencies {
    implementation(project(":model"))
}
