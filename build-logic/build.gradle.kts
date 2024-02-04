plugins {
    `kotlin-dsl`
    `kotlin-dsl-precompiled-script-plugins`
}

dependencies {
    implementation(libs.android.gradlePlugin)
    implementation(libs.kotlin.gradlePlugin)
    implementation(libs.verify.detektPlugin)
}

gradlePlugin {
    plugins {
        register("androidHilt") {
            id = "lifemash.android.hilt"
            implementationClass = "org.bmsk.lifemash.HiltAndroidPlugin"
        }
        register("kotlinHilt") {
            id = "lifemash.kotlin.hilt"
            implementationClass = "org.bmsk.lifemash.HiltKotlinPlugin"
        }
    }
}
