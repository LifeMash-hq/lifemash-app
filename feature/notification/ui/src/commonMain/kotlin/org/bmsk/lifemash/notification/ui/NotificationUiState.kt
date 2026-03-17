package org.bmsk.lifemash.notification.ui

import kotlinx.collections.immutable.PersistentList
import org.bmsk.lifemash.notification.domain.model.NotificationKeyword

internal sealed interface NotificationUiState {
    data object Loading : NotificationUiState
    data class Loaded(val keywords: PersistentList<NotificationKeyword>) : NotificationUiState
    data object Empty : NotificationUiState
}
