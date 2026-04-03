import com.android.build.api.dsl.ApplicationExtension
import org.bmsk.lifemash.configureKotest
import org.bmsk.lifemash.configureKotlinAndroid
import org.bmsk.lifemash.libs
import org.gradle.kotlin.dsl.configure

plugins {
    id("com.android.application")
    id("lifemash.android.compose")
    id("lifemash.android.lint")
}

configureKotlinAndroid()
configureKotest()

extensions.configure<ApplicationExtension> {
    namespace = "org.bmsk.lifemash"

    defaultConfig {
        applicationId = "org.bmsk.lifemash"
        versionCode = 1
        versionName = "1.3.0"
    }
}
