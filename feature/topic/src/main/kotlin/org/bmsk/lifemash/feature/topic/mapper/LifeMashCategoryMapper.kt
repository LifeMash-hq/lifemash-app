package org.bmsk.lifemash.feature.topic.mapper

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.res.stringResource
import org.bmsk.lifemash.core.model.section.LifeMashCategory
import org.bmsk.lifemash.feature.topic.R

@Composable
@ReadOnlyComposable
internal fun LifeMashCategory.stringResource(): String {
    return stringResource(
        when (this) {
            LifeMashCategory.BUSINESS -> R.string.feature_topic_category_business
            LifeMashCategory.ENTERTAINMENT -> R.string.feature_topic_category_entertainment
            LifeMashCategory.GENERAL -> R.string.feature_topic_category_general
            LifeMashCategory.HEALTH -> R.string.feature_topic_category_health
            LifeMashCategory.SCIENCE -> R.string.feature_topic_category_science
            LifeMashCategory.SPORTS -> R.string.feature_topic_category_sports
            LifeMashCategory.TECHNOLOGY -> R.string.feature_topic_category_technology
        }
    )
}