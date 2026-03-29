package org.bmsk.lifemash

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.string.shouldNotContain
import java.io.File

class ModuleIntegrityTest : StringSpec({

    val rootDir = File(System.getProperty("user.dir")).let { dir ->
        if (dir.name == "app") dir.parentFile else dir
    }

    "settings_gradle에 home_모듈이_없다" {
        val content = File(rootDir, "settings.gradle.kts").readText()
        content shouldNotContain "feature:home"
    }

    "settings_gradle에 assistant_모듈이_없다" {
        val content = File(rootDir, "settings.gradle.kts").readText()
        content shouldNotContain "feature:assistant"
    }

    "settings_gradle에 webview_모듈이_없다" {
        val content = File(rootDir, "settings.gradle.kts").readText()
        content shouldNotContain "shared:webview"
    }

    "DI_모듈에_home_관련_모듈이_없다" {
        val content = File(rootDir, "feature/main/src/commonMain/kotlin/org/bmsk/lifemash/main/di/AppKoinModules.kt").readText()
        content shouldNotContain "homeUiModule"
        content shouldNotContain "homeDataModule"
        content shouldNotContain "homeDomainModule"
    }

    "DI_모듈에_assistant_관련_모듈이_없다" {
        val content = File(rootDir, "feature/main/src/commonMain/kotlin/org/bmsk/lifemash/main/di/AppKoinModules.kt").readText()
        content shouldNotContain "assistantUiModule"
        content shouldNotContain "assistantDataModule"
        content shouldNotContain "assistantDomainModule"
    }
})
