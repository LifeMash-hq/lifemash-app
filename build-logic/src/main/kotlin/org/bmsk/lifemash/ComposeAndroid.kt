package org.bmsk.lifemash

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.findByType

internal fun Project.configureComposeAndroid() {
    with(plugins) {
        apply("org.jetbrains.kotlin.plugin.compose")
    }

    extensions.findByType<LibraryExtension>()?.buildFeatures { compose = true }
        ?: extensions.findByType<ApplicationExtension>()?.buildFeatures { compose = true }

    val libs = extensions.libs
    dependencies {
        val bom = libs.findLibrary("androidx-compose-bom").get()
        add("implementation", platform(bom))
        add("androidTestImplementation", platform(bom))

        add("implementation", libs.findLibrary("androidx.compose.material3").get())
        add("implementation", libs.findLibrary("androidx.compose.ui").get())
        add("implementation", libs.findLibrary("androidx.compose.ui.tooling.preview").get())
        add("androidTestImplementation", libs.findLibrary("androidx.test.ext").get())
        add("androidTestImplementation", libs.findLibrary("androidx.test.espresso.core").get())
        add("androidTestImplementation", libs.findLibrary("androidx.compose.ui.test").get())
        add("debugImplementation", libs.findLibrary("androidx.compose.ui.tooling").get())
        add("debugImplementation", libs.findLibrary("androidx.compose.ui.testManifest").get())
    }
}
