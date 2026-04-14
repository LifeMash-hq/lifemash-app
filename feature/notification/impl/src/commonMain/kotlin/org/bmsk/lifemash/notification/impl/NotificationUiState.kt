package org.bmsk.lifemash.notification.impl

import kotlinx.collections.immutable.PersistentList
import org.bmsk.lifemash.domain.notification.Notification

internal sealed interface NotificationUiState {
    data object Loading : NotificationUiState
    data class Loaded(val notifications: PersistentList<Notification>) : NotificationUiState
    data object Empty : NotificationUiState
    data class Error(val message: String) : NotificationUiState
}
