package org.bmsk.lifemash.assistant.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun AssistantRouteScreen(
    onShowErrorSnackbar: (Throwable?) -> Unit,
    onBack: () -> Unit,
    viewModel: AssistantViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            onShowErrorSnackbar(Exception(it))
            viewModel.clearError()
        }
    }

    AssistantScreen(
        uiState = uiState,
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
