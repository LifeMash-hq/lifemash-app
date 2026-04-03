package org.bmsk.lifemash.feature.shared.common

import androidx.compose.runtime.Composable

@Composable
expect fun rememberAddToCalendarLauncher(): (
    title: String,
    startMillis: Long,
    endMillis: Long?,
    location: String?,
    description: String?,
) -> Unit
