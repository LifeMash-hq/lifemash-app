package org.bmsk.lifemash.notification.impl

import kotlinx.collections.immutable.PersistentList

internal sealed interface NotificationUiState {
    data object Loading : NotificationUiState
    data class Loaded(val notifications: PersistentList<NotificationUi>) : NotificationUiState
    data object Empty : NotificationUiState
    data class Error(val message: String) : NotificationUiState
}
