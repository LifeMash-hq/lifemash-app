package org.bmsk.lifemash.lint.designsystem

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.Category
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.JavaContext
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity
import org.jetbrains.uast.UCallExpression
import org.jetbrains.uast.UElement
import org.jetbrains.uast.UQualifiedReferenceExpression

class DesignSystemDetector : Detector(), Detector.UastScanner {

    override fun getApplicableUastTypes(): List<Class<out UElement>> = listOf(
        UCallExpression::class.java,
        UQualifiedReferenceExpression::class.java,
    )

    override fun createUastHandler(context: JavaContext): UElementHandler =
        object : UElementHandler() {
            override fun visitCallExpression(node: UCallExpression) {
                val name = node.methodName ?: return
                val preferredName = METHOD_NAMES[name] ?: return
                reportIssue(context, node, name, preferredName)
            }

            override fun visitQualifiedReferenceExpression(node: UQualifiedReferenceExpression) {
                val name = node.receiver.asRenderString()
                val preferredName = RECEIVER_NAMES[name] ?: return
                reportIssue(context, node, name, preferredName)
            }
        }

    companion object {
        val ISSUE: Issue = Issue.create(
            id = "DesignSystem",
            briefDescription = "LifeMash 디자인시스템 컴포넌트를 사용하세요",
            explanation = "Material3 컴포넌트를 직접 사용하지 말고, " +
                "shared:designsystem 모듈의 래퍼 컴포넌트를 사용하세요. " +
                "일관된 디자인을 유지하기 위한 규칙입니다.",
            category = Category.CUSTOM_LINT_CHECKS,
            priority = 7,
            severity = Severity.WARNING,
            implementation = Implementation(
                DesignSystemDetector::class.java,
                Scope.JAVA_FILE_SCOPE,
            ),
        )

        val METHOD_NAMES = mapOf(
            "MaterialTheme" to "LifeMashTheme",
            "AsyncImage" to "NetworkImage",
        )

        val RECEIVER_NAMES = emptyMap<String, String>()

        fun reportIssue(
            context: JavaContext,
            node: UElement,
            name: String,
            preferredName: String,
        ) {
            context.report(
                ISSUE,
                node,
                context.getLocation(node),
                "$name 대신 $preferredName 를 사용하세요",
            )
        }
    }
}
