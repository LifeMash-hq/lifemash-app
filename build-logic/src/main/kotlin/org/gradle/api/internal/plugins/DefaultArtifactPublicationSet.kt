package org.gradle.api.internal.plugins

/**
 * Stub for Gradle 9.x compatibility.
 *
 * The Kotlin DSL accessor generator references this internal Gradle class
 * (removed in Gradle 9.x) in generated accessor files. This stub satisfies
 * the Kotlin compiler without affecting runtime behaviour, since the generated
 * accessors are `internal` and never invoked by build scripts.
 */
open class DefaultArtifactPublicationSet
