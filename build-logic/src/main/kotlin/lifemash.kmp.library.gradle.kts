import org.bmsk.lifemash.configureKmpLibrary

plugins {
    kotlin("multiplatform")
    id("lifemash.verify.detekt")
}

// com.android.library은 plugins {} 밖에서 apply:
// accessor 생성 시 "com.android.library + kotlin.multiplatform 비호환" 체크를 건너뜁니다.
// 실제 모듈에 적용될 때는 gradle.properties(android.builtInKotlin=false, android.newDsl=false)로 동작합니다.
apply(plugin = "com.android.library")

configureKmpLibrary()
