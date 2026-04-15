buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath(libs.oss.licenses.plugin)
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.serialization) apply false
alias(libs.plugins.verify.detekt) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.firebase.crashlytics) apply false
    alias(libs.plugins.shadow) apply false
    alias(libs.plugins.dokka) apply false
}

subprojects {
    // Kotlin 플러그인이 적용된 모듈에만 Dokka 적용.
    // include(":feature:moment:api") 등으로 암묵 생성되는 컨테이너 프로젝트
    // (:feature:moment, :data 등)는 소스가 없으므로 제외.
    listOf(
        "org.jetbrains.kotlin.multiplatform",
        "org.jetbrains.kotlin.android",
        "org.jetbrains.kotlin.jvm",
    ).forEach { kotlinPluginId ->
        pluginManager.withPlugin(kotlinPluginId) {
            apply(plugin = "org.jetbrains.dokka")
        }
    }
    configurations.all {
        exclude(group = "com.google.firebase", module = "firebase-common-ktx")
    }
}

apply {
    from("gradle/dependencyGraph.gradle")
    from("gradle/check-inline-fqn.gradle.kts")
    from("gradle/check-param-format.gradle.kts")
}
