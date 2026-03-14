package org.bmsk.lifemash.lint

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.client.api.Vendor
import com.android.tools.lint.detector.api.CURRENT_API
import org.bmsk.lifemash.lint.designsystem.DesignSystemDetector

class LifeMashIssueRegistry : IssueRegistry() {

    override val issues = listOf(
        DesignSystemDetector.ISSUE,
    )

    override val api: Int = CURRENT_API

    override val minApi: Int = 12

    override val vendor: Vendor = Vendor(
        vendorName = "LifeMash",
        feedbackUrl = "https://github.com/YiBeomSeok/LifeMash-App/issues",
        contact = "https://github.com/YiBeomSeok/LifeMash-App",
    )
}
