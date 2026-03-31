import org.bmsk.lifemash.configureComposeLint
import org.bmsk.lifemash.configureKmpCompose

plugins {
    id("lifemash.kmp.library")
    id("org.jetbrains.compose")
}

apply(plugin = "org.jetbrains.kotlin.plugin.compose")

configureComposeLint()
configureKmpCompose()
