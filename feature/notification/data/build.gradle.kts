import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

plugins {
    id("lifemash.kmp.library")
    alias(libs.plugins.ksp)
}

kotlin {
    android {
        namespace = "org.bmsk.lifemash.notification.data"
    }

    sourceSets {
        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.coroutines.test)
        }
        commonMain.dependencies {
            implementation(projects.feature.notification.domain)
            implementation(libs.coroutines.core)
            implementation(libs.kotlinx.datetime)
            implementation(libs.koin.core)
            implementation(libs.androidx.room.runtime)
            implementation(libs.androidx.sqlite.bundled)
            implementation(libs.gitlive.firebase.firestore)
        }
        androidMain.dependencies {
            implementation(project.dependencies.platform(libs.firebase.bom))
        }
    }
}

dependencies {
    // Room KMP: kspCommonMainMetadata(메타데이터 처리) + kspAndroid/iOS(실제 구현 생성)
    add("kspCommonMainMetadata", libs.androidx.room.compiler)
    add("kspAndroid", libs.androidx.room.compiler)
    add("kspIosArm64", libs.androidx.room.compiler)
    add("kspIosSimulatorArm64", libs.androidx.room.compiler)
}

// Room KMP: kspAndroid는 kspCommonMainMetadata 완료 후 실행
tasks.matching { it.name.startsWith("ksp") && it.name != "kspCommonMainKotlinMetadata" }.configureEach {
    dependsOn("kspCommonMainKotlinMetadata")
}
tasks.withType<KotlinCompilationTask<*>>().configureEach {
    if (name != "kspCommonMainKotlinMetadata") {
        dependsOn("kspCommonMainKotlinMetadata")
    }
}
