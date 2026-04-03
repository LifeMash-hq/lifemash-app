plugins {
    `kotlin-dsl`
    `kotlin-dsl-precompiled-script-plugins`
}

dependencies {
    implementation(libs.android.gradlePlugin)
    implementation(libs.kotlin.gradlePlugin)
    implementation(libs.kotlin.serializationPlugin)
    implementation(libs.verify.detektPlugin)
    implementation(libs.compose.gradlePlugin)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}
