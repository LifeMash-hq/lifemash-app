import com.android.build.api.dsl.LibraryExtension
import org.bmsk.lifemash.configureHiltAndroid
import org.bmsk.lifemash.libs
import org.gradle.kotlin.dsl.configure

plugins {
    id("lifemash.android.library")
    id("lifemash.android.compose")
}

extensions.configure<LibraryExtension> {
    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}

configureHiltAndroid()

dependencies {
    implementation(project(":shared:designsystem"))
    implementation(project(":model"))

    val libs = project.extensions.libs
    implementation(libs.findLibrary("hilt.navigation.compose").get())
    implementation(libs.findLibrary("androidx.compose.navigation").get())
    androidTestImplementation(libs.findLibrary("androidx.compose.navigation.test").get())

    implementation(libs.findLibrary("androidx.lifecycle.viewModelCompose").get())
    implementation(libs.findLibrary("androidx.lifecycle.runtimeCompose").get())
}
