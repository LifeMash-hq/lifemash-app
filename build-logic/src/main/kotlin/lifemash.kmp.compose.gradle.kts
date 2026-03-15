plugins {
    id("org.jetbrains.compose")
}

// kmp.library + compose compiler는 plugins {} 밖에서 apply
apply(plugin = "lifemash.kmp.library")
apply(plugin = "org.jetbrains.kotlin.plugin.compose")
