// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    dependencies {
        classpath(libs.androidx.navigation.sageargs)
    }
}

plugins {
    id (libs.plugins.android.application.get().pluginId) version libs.versions.agp apply false
    id (libs.plugins.android.library.get().pluginId) version libs.versions.agp apply false
    id (libs.plugins.kotlin.android.get().pluginId) version libs.versions.kotlin apply false
    id (libs.plugins.kotlin.kapt.get().pluginId) version libs.versions.kotlin apply false
    id (libs.plugins.kotlin.jvm.get().pluginId) version libs.versions.kotlin apply false
    id (libs.plugins.hilt.android.get().pluginId) version libs.versions.hilt apply false
}