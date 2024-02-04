import gradle.kotlin.dsl.accessors._9885c8525475a2a77e0b650bdf1e3c81.implementation
import org.bmsk.lifemash.configureHiltAndroid
import org.bmsk.lifemash.configureKotestAndroid
import org.bmsk.lifemash.configureKotlinAndroid
import org.bmsk.lifemash.libs

plugins {
    id("com.android.application")
    id("lifemash.android.compose")
}

configureKotlinAndroid()
configureHiltAndroid()
configureKotestAndroid()

dependencies {
    val libs = project.extensions.libs
    implementation(libs.findLibrary("androidx.compose.navigation").get())

    implementation(libs.findLibrary("androidx.lifecycle.viewModelCompose").get())
    implementation(libs.findLibrary("androidx.lifecycle.runtimeCompose").get())
}
