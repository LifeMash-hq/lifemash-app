import org.bmsk.lifemash.configureKotest
import org.bmsk.lifemash.configureKotlin

plugins {
    kotlin("jvm")
    id("lifemash.verify.detekt")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

configureKotlin()
configureKotest()
