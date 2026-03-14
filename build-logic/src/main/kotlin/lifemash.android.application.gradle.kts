import com.android.build.api.dsl.ApplicationExtension
import org.bmsk.lifemash.configureHiltAndroid
import org.bmsk.lifemash.configureKotestAndroid
import org.bmsk.lifemash.configureKotlinAndroid
import org.bmsk.lifemash.libs
import org.gradle.kotlin.dsl.configure

plugins {
    id("com.android.application")
    id("lifemash.android.compose")
    id("lifemash.android.lint")
}

configureKotlinAndroid()
configureHiltAndroid()
configureKotestAndroid()

extensions.configure<ApplicationExtension> {
    namespace = "org.bmsk.lifemash"

    defaultConfig {
        applicationId = "org.bmsk.lifemash"
        versionCode = 1
        versionName = "1.3.0"
    }
}

dependencies {
    val libs = project.extensions.libs
    implementation(libs.findLibrary("androidx.compose.navigation").get())

    implementation(libs.findLibrary("androidx.lifecycle.viewModelCompose").get())
    implementation(libs.findLibrary("androidx.lifecycle.runtimeCompose").get())

    lintChecks(project(":lint"))
}
