package org.bmsk.lifemash

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

internal fun Project.configureDagger() {
    with(pluginManager) {
        apply("com.google.devtools.ksp")
    }

    val libs = extensions.libs
    dependencies {
        "implementation"(libs.findLibrary("dagger").get())
        "ksp"(libs.findLibrary("dagger.compiler").get())
    }
}

internal class DaggerPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            configureDagger()
        }
    }
}
