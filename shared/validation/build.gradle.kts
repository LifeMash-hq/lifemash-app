plugins {
    id("lifemash.kmp.library")
}

kotlin {
    jvm()

    android {
        namespace = "org.bmsk.lifemash.validation"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.datetime)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}
