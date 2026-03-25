plugins {
    id("lifemash.kmp.library")
}

kotlin {
    android {
        namespace = "org.bmsk.lifemash.validation"
    }

    jvm()

    sourceSets {
        commonMain.dependencies { }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}
