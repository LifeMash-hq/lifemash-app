buildscript {
    dependencies {
        classpath(libs.androidx.navigation.sageargs)
        classpath("com.android.tools.build:gradle:8.2.2")
    }
}

plugins {
    id (libs.plugins.android.application.get().pluginId) version libs.versions.agp apply false
    id (libs.plugins.android.library.get().pluginId) version libs.versions.agp apply false
    id (libs.plugins.kotlin.android.get().pluginId) version libs.versions.kotlin apply false
    id (libs.plugins.kotlin.jvm.get().pluginId) version libs.versions.kotlin apply false
    id (libs.plugins.hilt.android.get().pluginId) version libs.versions.hilt apply false
}