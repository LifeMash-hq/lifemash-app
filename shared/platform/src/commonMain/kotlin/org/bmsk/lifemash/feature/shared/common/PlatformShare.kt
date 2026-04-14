package org.bmsk.lifemash.feature.shared.common

import androidx.compose.runtime.Composable

@Composable
expect fun rememberShareLauncher(): (text: String) -> Unit
