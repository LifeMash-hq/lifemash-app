package org.bmsk.lifemash

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.findByType

internal fun Project.configureKotestAndroid() {
    configureKotest()
    configureJUnitAndroid()
}

internal fun Project.configureJUnitAndroid() {
    extensions.findByType<LibraryExtension>()?.testOptions {
        unitTests.all { it.useJUnitPlatform() }
    } ?: extensions.findByType<ApplicationExtension>()?.testOptions {
        unitTests.all { it.useJUnitPlatform() }
    }
}
