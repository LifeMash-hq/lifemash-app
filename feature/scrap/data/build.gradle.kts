import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

plugins {
    id("lifemash.kmp.library")
    alias(libs.plugins.ksp)
}

kotlin {
    android {
        namespace = "org.bmsk.lifemash.scrap.data"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.shared.model)
            implementation(projects.feature.scrap.domain)
            implementation(libs.coroutines.core)
            implementation(libs.kotlinx.datetime)
            implementation(libs.koin.core)
            implementation(libs.androidx.room.runtime)
            implementation(libs.androidx.sqlite.bundled)
        }
    }
}

dependencies {
    add("kspCommonMainMetadata", libs.androidx.room.compiler)
    add("kspAndroid", libs.androidx.room.compiler)
    add("kspIosArm64", libs.androidx.room.compiler)
    add("kspIosSimulatorArm64", libs.androidx.room.compiler)
}

tasks.matching { it.name.startsWith("ksp") && it.name != "kspCommonMainKotlinMetadata" }.configureEach {
    dependsOn("kspCommonMainKotlinMetadata")
}
tasks.withType<KotlinCompilationTask<*>>().configureEach {
    if (name != "kspCommonMainKotlinMetadata") {
        dependsOn("kspCommonMainKotlinMetadata")
    }
}
