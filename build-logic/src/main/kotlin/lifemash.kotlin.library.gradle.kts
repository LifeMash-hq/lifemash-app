import org.bmsk.lifemash.configureKotest
import org.bmsk.lifemash.configureKotlin

plugins {
    kotlin("jvm")
    id("lifemash.verify.detekt")
}

configureKotlin()
configureKotest()
