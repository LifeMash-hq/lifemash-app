/**
 * Inline FQN (Fully Qualified Name) detector & auto-fixer.
 *
 * Applied from root build.gradle.kts:
 *   apply(from = "gradle/check-inline-fqn.gradle.kts")
 *
 * Usage:
 *   ./gradlew checkInlineFqn   — 검출만 (CI용, 위반 시 빌드 실패)
 *   ./gradlew fixInlineFqn     — 자동 수정 (import 추가 + 인라인 FQN → 단순명 치환)
 */

// ── shared helpers ──

// 3+ package segments followed by a capitalized identifier (e.g. org.bmsk.lifemash.Foo)
val fqnPattern = Regex("""(?<![.\w])[a-z][a-z0-9]*(\.[a-z][a-z0-9]*){2,}\.[A-Z]\w*""")
val skipLinePattern = Regex("""^\s*(import\s|package\s|//|/?\*|\*)""")
val stringLiteralPattern = Regex(""""[^"]*"""")
// IDE artifact prefix that sometimes leaks into code
val rootIdePrefix = "_root_ide_package_."

fun kotlinSources(): List<File> =
    fileTree(rootDir) {
        include("**/*.kt")
        exclude("**/build/**", "**/buildSrc/**", "**/build-logic/**", "**/*.gradle.kts")
    }.sorted()

data class FqnHit(
    val file: File,
    val lineNumber: Int,
    val column: Int,
    val fqn: String,
) {
    /** e.g. "org.bmsk.lifemash.domain.model.calendar.EventVisibility" → "EventVisibility" */
    val simpleName: String get() = fqn.substringAfterLast('.')
}

fun scanFqnHits(): List<FqnHit> {
    val hits = mutableListOf<FqnHit>()
    kotlinSources().forEach { file ->
        file.readLines().forEachIndexed { index, line ->
            if (!skipLinePattern.containsMatchIn(line)) {
                val cleaned = stringLiteralPattern.replace(line, "\"\"")
                fqnPattern.findAll(cleaned).forEach { match ->
                    hits += FqnHit(file, index + 1, match.range.first + 1, match.value)
                }
            }
        }
    }
    return hits
}

// ── checkInlineFqn (검출만) ──

tasks.register("checkInlineFqn") {
    group = "verification"
    description = "Detects inline Fully Qualified Name usage in Kotlin source files"

    doLast {
        val hits = scanFqnHits()
        if (hits.isNotEmpty()) {
            logger.warn("\n⚠ Inline FQN usage detected (${hits.size} occurrences):\n")
            hits.forEach { logger.warn("  ${it.file.relativeTo(rootDir)}:${it.lineNumber}:${it.column}  ${it.fqn}") }
            logger.warn("\n→ import 문으로 대체하세요. (./gradlew fixInlineFqn 으로 자동 수정 가능)\n")
            throw GradleException("Found ${hits.size} inline FQN usage(s). Use imports instead.")
        } else {
            logger.lifecycle("✓ No inline FQN usage found.")
        }
    }
}

// ── fixInlineFqn (자동 수정) ──

tasks.register("fixInlineFqn") {
    group = "verification"
    description = "Auto-fixes inline FQN usages: adds imports and replaces with simple names"

    doLast {
        val hits = scanFqnHits()
        if (hits.isEmpty()) {
            logger.lifecycle("✓ No inline FQN usage found. Nothing to fix.")
            return@doLast
        }

        // Group by file
        val hitsByFile = hits.groupBy { it.file }
        var fixedTotal = 0
        val conflictWarnings = mutableListOf<String>()

        hitsByFile.forEach { (file, fileHits) ->
            val lines = file.readLines().toMutableList()

            // Collect existing imports
            val existingImports = lines
                .filter { it.trimStart().startsWith("import ") }
                .map { it.trim().removePrefix("import ").trim() }
                .toMutableSet()

            // Find the last import line index (to insert new imports after it)
            val lastImportIndex = lines.indexOfLast { it.trimStart().startsWith("import ") }
            // If no imports, insert after package + blank line
            val packageIndex = lines.indexOfFirst { it.trimStart().startsWith("package ") }
            val insertIndex = when {
                lastImportIndex >= 0 -> lastImportIndex
                packageIndex >= 0 -> packageIndex + 1
                else -> 0
            }

            // Collect unique FQNs to add as imports
            // Check for name conflicts: multiple different FQNs with the same simple name
            val fqnsBySimpleName = fileHits.map { it.fqn }.distinct().groupBy { it.substringAfterLast('.') }
            val newImports = mutableListOf<String>()

            val safeToReplace = mutableSetOf<String>() // FQNs safe to replace with simple name
            fqnsBySimpleName.forEach { (simpleName, fqns) ->
                if (fqns.size > 1) {
                    // Name conflict — can't auto-fix, warn
                    conflictWarnings += "  ${file.relativeTo(rootDir)}: '$simpleName' → ${fqns.joinToString()}"
                } else {
                    val fqn = fqns.single()
                    safeToReplace += fqn
                    // Also check if existing imports already have a different class with the same simple name
                    val conflictingImport = existingImports.find {
                        it.substringAfterLast('.') == simpleName && it != fqn
                    }
                    if (conflictingImport != null) {
                        conflictWarnings += "  ${file.relativeTo(rootDir)}: '$simpleName' conflicts with existing import '$conflictingImport'"
                        safeToReplace -= fqn
                    } else if (fqn !in existingImports) {
                        newImports += fqn
                    }
                }
            }

            if (safeToReplace.isEmpty() && newImports.isEmpty()) return@forEach

            // Replace inline FQNs with simple names in code lines (not import/package lines)
            var fixedInFile = 0
            for (i in lines.indices) {
                if (skipLinePattern.containsMatchIn(lines[i])) continue
                var line = lines[i]
                // Also strip _root_ide_package_. prefix
                line = line.replace(rootIdePrefix, "")
                for (fqn in safeToReplace) {
                    val simpleName = fqn.substringAfterLast('.')
                    if (fqn in line) {
                        val count = Regex(Regex.escape(fqn)).findAll(line).count()
                        line = line.replace(fqn, simpleName)
                        fixedInFile += count
                    }
                }
                lines[i] = line
            }

            // Also clean up any remaining _root_ide_package_. in import lines
            for (i in lines.indices) {
                if (lines[i].contains(rootIdePrefix)) {
                    lines[i] = lines[i].replace(rootIdePrefix, "")
                }
            }

            // Insert new imports
            if (newImports.isNotEmpty()) {
                val sorted = newImports.sorted()
                val insertAt = if (lastImportIndex >= 0) lastImportIndex + 1 else insertIndex + 1
                // Ensure blank line before imports if inserting after package
                if (lastImportIndex < 0 && insertAt > 0 && lines.getOrNull(insertAt - 1)?.isNotBlank() == true) {
                    lines.add(insertAt, "")
                    sorted.forEachIndexed { idx, imp -> lines.add(insertAt + 1 + idx, "import $imp") }
                } else {
                    sorted.forEachIndexed { idx, imp -> lines.add(insertAt + idx, "import $imp") }
                }
            }

            file.writeText(lines.joinToString("\n"))
            fixedTotal += fixedInFile
            logger.lifecycle("  ✓ ${file.relativeTo(rootDir)}: $fixedInFile replacement(s), ${newImports.size} import(s) added")
        }

        logger.lifecycle("\n✓ Fixed $fixedTotal inline FQN usage(s) across ${hitsByFile.size} file(s).")
        if (conflictWarnings.isNotEmpty()) {
            logger.warn("\n⚠ Skipped (name conflicts — fix manually):\n")
            conflictWarnings.forEach { logger.warn(it) }
        }

        // Verify
        val remaining = scanFqnHits()
        if (remaining.isNotEmpty()) {
            logger.warn("\n⚠ ${remaining.size} remaining inline FQN(s) after auto-fix (manual fix needed):")
            remaining.forEach { logger.warn("  ${it.file.relativeTo(rootDir)}:${it.lineNumber}:${it.column}  ${it.fqn}") }
        }
    }
}
