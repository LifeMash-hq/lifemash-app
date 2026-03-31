package org.bmsk.lifemash.auth.ui

import androidx.compose.runtime.Composable

@Composable
internal expect fun rememberKakaoLoginLauncher(onResult: (Result<String>) -> Unit): () -> Unit

@Composable
internal expect fun rememberGoogleLoginLauncher(onResult: (Result<String>) -> Unit): () -> Unit
