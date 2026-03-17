import org.bmsk.lifemash.configureKmpLibrary

plugins {
    kotlin("multiplatform")
    id("com.android.kotlin.multiplatform.library")
    id("lifemash.verify.detekt")
}

configureKmpLibrary()
