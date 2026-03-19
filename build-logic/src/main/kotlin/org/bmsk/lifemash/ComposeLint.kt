package org.bmsk.lifemash

import com.android.build.api.dsl.Lint
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

internal fun Project.configureComposeLint() {
    pluginManager.apply("com.android.lint")

    configure<Lint> {
        lintConfig = rootProject.file("lint.xml")
    }

    val libs = extensions.libs
    dependencies {
        add("lintChecks", libs.findLibrary("compose-lint-checks").get())
    }
}
