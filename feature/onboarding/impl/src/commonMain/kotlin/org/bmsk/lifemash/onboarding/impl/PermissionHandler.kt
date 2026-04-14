package org.bmsk.lifemash.onboarding.impl

import androidx.compose.runtime.Composable

@Composable
expect fun rememberCalendarPermissionLauncher(onResult: (Boolean) -> Unit): () -> Unit

@Composable
expect fun rememberNotificationPermissionLauncher(onResult: (Boolean) -> Unit): () -> Unit
