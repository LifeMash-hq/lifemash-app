package org.bmsk.lifemash.assistant.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun AssistantRouteScreen(
    onShowErrorSnackbar: (Throwable?) -> Unit,
    onBack: () -> Unit,
    viewModel: AssistantViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadApiKeyStatus()
    }

    when (val state = uiState) {
        is AssistantUiState.Loading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is AssistantUiState.Ready -> {
            LaunchedEffect(state.error) {
                state.error?.let {
                    onShowErrorSnackbar(Exception(it))
                    viewModel.clearError()
                }
            }

            AssistantScreen(
                uiState = state,
                onInputChange = viewModel::updateInputText,
                onSend = viewModel::sendMessage,
                onBack = onBack,
                onToggleSettings = viewModel::toggleSettings,
                onSaveApiKey = viewModel::saveApiKey,
                onRemoveApiKey = viewModel::removeApiKey,
                onLoadConversations = viewModel::loadConversations,
                onLoadConversation = viewModel::loadConversation,
                onDeleteConversation = viewModel::deleteConversation,
                onNewConversation = viewModel::newConversation,
            )
        }
    }
}
