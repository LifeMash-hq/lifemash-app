/**
 * Parameter formatting enforcer.
 * 파라미터가 3개 이상이면 각 파라미터를 별도의 줄에 배치하도록 강제한다.
 *
 * Applied from root build.gradle.kts:
 *   apply(from = "gradle/check-param-format.gradle.kts")
 *
 * Usage:
 *   ./gradlew checkParamFormat   — 검출만 (CI용, 위반 시 빌드 실패)
 *   ./gradlew fixParamFormat     — 자동 수정
 */

// ── helpers ──

fun kotlinSources(): List<File> =
    fileTree(rootDir) {
        include("**/*.kt")
        exclude("**/build/**", "**/buildSrc/**", "**/build-logic/**", "**/*.gradle.kts")
    }.sorted()

data class Violation(
    val file: File,
    val line: Int,
    val openOffset: Int,
    val closeOffset: Int,
    val params: List<String>,
    val baseIndent: String,
)

/** Split content between ( and ) by top-level commas. */
fun splitTopLevel(content: String): List<String> {
    val params = mutableListOf<String>()
    var depth = 0
    var inStr = false
    var inRaw = false
    var start = 0
    var i = 0
    while (i < content.length) {
        val c = content[i]
        when {
            !inStr && content.startsWith("\"\"\"", i) -> { inRaw = !inRaw; i += 2 }
            !inRaw && c == '\\' && inStr -> i++ // skip escaped
            !inRaw && c == '"' -> inStr = !inStr
            inStr || inRaw -> {}
            c == '(' || c == '<' || c == '[' || c == '{' -> depth++
            c == ')' || c == '>' || c == ']' || c == '}' -> depth--
            c == ',' && depth == 0 -> {
                params += content.substring(start, i)
                start = i + 1
            }
        }
        i++
    }
    if (start <= content.length) {
        val last = content.substring(start).trim()
        if (last.isNotEmpty()) params += content.substring(start)
    }
    return params
}

/**
 * Scan text for ( that is NOT inside a string, comment, or annotation.
 * Returns the offset of ( or -1 to skip.
 */
fun findViolations(text: String, file: File): List<Violation> {
    val results = mutableListOf<Violation>()
    var i = 0
    var line = 1
    var lineStart = 0

    while (i < text.length) {
        val c = text[i]

        // Track lines
        if (c == '\n') { line++; lineStart = i + 1; i++; continue }

        // Skip raw strings
        if (text.startsWith("\"\"\"", i)) {
            i += 3
            while (i < text.length && !text.startsWith("\"\"\"", i)) {
                if (text[i] == '\n') { line++; lineStart = i + 1 }
                i++
            }
            if (i < text.length) i += 3
            continue
        }

        // Skip strings
        if (c == '"') {
            i++
            while (i < text.length && text[i] != '"' && text[i] != '\n') {
                if (text[i] == '\\') i++
                i++
            }
            if (i < text.length && text[i] == '"') i++
            continue
        }

        // Skip line comments
        if (text.startsWith("//", i)) {
            while (i < text.length && text[i] != '\n') i++
            continue
        }

        // Skip block comments
        if (text.startsWith("/*", i)) {
            i += 2
            while (i < text.length && !text.startsWith("*/", i)) {
                if (text[i] == '\n') { line++; lineStart = i + 1 }
                i++
            }
            if (i < text.length) i += 2
            continue
        }

        // Found (
        if (c == '(') {
            val openOffset = i
            val openLine = line
            val lineText = text.substring(lineStart, text.indexOf('\n', lineStart).let { if (it == -1) text.length else it })
            val baseIndent = lineText.takeWhile { it == ' ' || it == '\t' }

            // Check if this is an annotation: look backwards for @AnnotationName
            val before = text.substring(maxOf(0, lineStart), openOffset).trimEnd()
            if (before.matches(Regex(""".*@\w+$"""))) {
                i++; continue
            }

            // Find matching )
            val contentStart = i + 1
            var depth = 1
            var j = contentStart
            while (j < text.length && depth > 0) {
                when {
                    text.startsWith("\"\"\"", j) -> {
                        j += 3
                        while (j < text.length && !text.startsWith("\"\"\"", j)) {
                            if (text[j] == '\n') { line++; lineStart = j + 1 }
                            j++
                        }
                        if (j < text.length) j += 2 // will be incremented below
                    }
                    text[j] == '"' -> {
                        j++
                        while (j < text.length && text[j] != '"' && text[j] != '\n') {
                            if (text[j] == '\\') j++
                            j++
                        }
                    }
                    text.startsWith("//", j) -> {
                        while (j < text.length && text[j] != '\n') j++
                        j-- // will be incremented below
                    }
                    text.startsWith("/*", j) -> {
                        j += 2
                        while (j < text.length && !text.startsWith("*/", j)) {
                            if (text[j] == '\n') { line++; lineStart = j + 1 }
                            j++
                        }
                        if (j < text.length) j++ // skip *, will get / below
                    }
                    text[j] == '(' -> depth++
                    text[j] == ')' -> depth--
                    text[j] == '\n' -> { line++; lineStart = j + 1 }
                }
                if (depth > 0) j++
            }

            if (depth != 0) { i++; continue } // unmatched paren

            val closeOffset = j
            val content = text.substring(contentStart, closeOffset)
            val params = splitTopLevel(content)

            if (params.size >= 3) {
                // Check if already properly formatted:
                // each param should be on its own line
                val contentLines = content.split("\n").filter { it.isNotBlank() }
                if (contentLines.size < params.size) {
                    results += Violation(file, openLine, openOffset, closeOffset, params, baseIndent)
                }
            }

            i = closeOffset + 1
            continue
        }

        i++
    }
    return results
}

fun formatBlock(v: Violation): String {
    val paramIndent = v.baseIndent + "    "
    val sb = StringBuilder()
    sb.append("\n")
    v.params.forEachIndexed { idx, raw ->
        val trimmed = raw.trim()
        sb.append(paramIndent).append(trimmed)
        // Always trailing comma
        if (!trimmed.endsWith(",")) sb.append(",")
        sb.append("\n")
    }
    sb.append(v.baseIndent)
    return sb.toString()
}

fun processFile(file: File, fix: Boolean): Triple<Int, Int, List<Violation>> {
    var text = file.readText()
    val violations = findViolations(text, file)
    if (violations.isEmpty() || !fix) return Triple(violations.size, 0, violations)

    // Fix from end to start
    var fixed = 0
    for (v in violations.reversed()) {
        val replacement = formatBlock(v)
        text = text.substring(0, v.openOffset + 1) + replacement + text.substring(v.closeOffset)
        fixed++
    }
    file.writeText(text)
    return Triple(violations.size, fixed, violations)
}

// ── checkParamFormat ──

tasks.register("checkParamFormat") {
    group = "verification"
    description = "Detects functions/constructors with 3+ params not on separate lines"

    doLast {
        var total = 0
        val allViolations = mutableListOf<Violation>()

        kotlinSources().forEach { file ->
            val (count, _, violations) = processFile(file, fix = false)
            total += count
            allViolations += violations
        }

        if (allViolations.isNotEmpty()) {
            logger.warn("\n⚠ Parameter format violations ($total occurrences):\n")
            allViolations.forEach { v ->
                logger.warn("  ${v.file.relativeTo(rootDir)}:${v.line}  ${v.params.size} params on same line")
            }
            logger.warn("\n→ 파라미터를 각 줄에 배치하세요. (./gradlew fixParamFormat 으로 자동 수정 가능)\n")
            throw GradleException("Found $total parameter format violation(s).")
        } else {
            logger.lifecycle("✓ All parameter lists properly formatted.")
        }
    }
}

// ── fixParamFormat ──

tasks.register("fixParamFormat") {
    group = "verification"
    description = "Auto-fixes parameter lists with 3+ params to use separate lines"

    doLast {
        var totalViolations = 0
        var totalFixed = 0
        var filesFixed = 0

        kotlinSources().forEach { file ->
            val (violations, fixed, _) = processFile(file, fix = true)
            if (fixed > 0) {
                totalViolations += violations
                totalFixed += fixed
                filesFixed++
                logger.lifecycle("  ✓ ${file.relativeTo(rootDir)}: $fixed block(s) reformatted")
            }
        }

        if (totalFixed > 0) {
            logger.lifecycle("\n✓ Fixed $totalFixed parameter block(s) across $filesFixed file(s).")

            // Verify
            var remaining = 0
            kotlinSources().forEach { file ->
                val (count, _, _) = processFile(file, fix = false)
                remaining += count
            }
            if (remaining > 0) {
                logger.warn("⚠ $remaining remaining violation(s) — may need manual fix or another pass.")
            }
        } else {
            logger.lifecycle("✓ All parameter lists already properly formatted.")
        }
    }
}
