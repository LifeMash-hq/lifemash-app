package org.bmsk.lifemash.lint.designsystem

import com.android.tools.lint.checks.infrastructure.LintDetectorTest
import com.android.tools.lint.checks.infrastructure.TestMode
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Issue

class DesignSystemDetectorTest : LintDetectorTest() {

    override fun getDetector(): Detector = DesignSystemDetector()

    override fun getIssues(): List<Issue> = listOf(DesignSystemDetector.ISSUE)

    fun testMaterialTheme_직접사용시_경고() {
        lint()
            .allowMissingSdk()
            .files(
                kotlin(
                    """
                    package org.bmsk.lifemash.feature.feed
                    fun MaterialTheme(content: () -> Unit) {}
                    fun MyScreen() {
                        MaterialTheme {}
                    }
                    """,
                ).indented(),
            )
            .testModes(TestMode.DEFAULT)
            .run()
            .expectWarningCount(1)
            .expectContains("MaterialTheme 대신 LifeMashTheme")
    }

    fun testAsyncImage_직접사용시_경고() {
        lint()
            .allowMissingSdk()
            .files(
                kotlin(
                    """
                    package org.bmsk.lifemash.feature.feed
                    fun AsyncImage(model: Any?) {}
                    fun MyScreen() {
                        AsyncImage(model = null)
                    }
                    """,
                ).indented(),
            )
            .testModes(TestMode.DEFAULT)
            .run()
            .expectWarningCount(1)
            .expectContains("AsyncImage 대신 NetworkImage")
    }

    fun testLifeMashTheme_사용시_정상() {
        lint()
            .allowMissingSdk()
            .files(
                kotlin(
                    """
                    package org.bmsk.lifemash.feature.feed
                    fun LifeMashTheme(content: () -> Unit) {}
                    fun MyScreen() {
                        LifeMashTheme {}
                    }
                    """,
                ).indented(),
            )
            .testModes(TestMode.DEFAULT)
            .run()
            .expectClean()
    }
}
