package org.bmsk.lifemash

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.plugins.ExtensionContainer
import org.gradle.kotlin.dsl.findByType
import org.gradle.kotlin.dsl.getByType

internal val Project.applicationExtension: ApplicationExtension
    get() = extensions.getByType<ApplicationExtension>()

internal val Project.libraryExtension: LibraryExtension
    get() = extensions.getByType<LibraryExtension>()

internal val Project.androidExtension: CommonExtension
    get() = extensions.findByType<LibraryExtension>()
        ?: extensions.findByType<ApplicationExtension>()
        ?: error("Could not find Library or Application extension from this project")

internal val ExtensionContainer.libs: VersionCatalog
    get() = getByType<VersionCatalogsExtension>().named("libs")
