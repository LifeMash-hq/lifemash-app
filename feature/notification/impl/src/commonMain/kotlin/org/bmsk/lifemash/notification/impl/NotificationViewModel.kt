package org.bmsk.lifemash.notification.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.bmsk.lifemash.domain.usecase.notification.GetNotificationsUseCase
import org.bmsk.lifemash.domain.usecase.notification.MarkNotificationReadUseCase

internal class NotificationViewModel(
    private val getNotificationsUseCase: GetNotificationsUseCase,
    private val markNotificationReadUseCase: MarkNotificationReadUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<NotificationUiState>(NotificationUiState.Loading)
    val uiState: StateFlow<NotificationUiState> = _uiState.asStateFlow()

    init {
        loadNotifications()
    }

    fun loadNotifications() {
        viewModelScope.launch {
            _uiState.value = NotificationUiState.Loading
            runCatching { getNotificationsUseCase() }
                .onSuccess { notifications ->
                    _uiState.value = if (notifications.isEmpty()) {
                        NotificationUiState.Empty
                    } else {
                        NotificationUiState.Loaded(notifications.toPersistentList())
                    }
                }
                .onFailure { e ->
                    _uiState.value = NotificationUiState.Error(e.message ?: "알림을 불러올 수 없습니다")
                }
        }
    }

    fun markAllAsRead() {
        val state = _uiState.value as? NotificationUiState.Loaded ?: return
        val unread = state.notifications.filter { !it.isRead }
        if (unread.isEmpty()) return

        viewModelScope.launch {
            unread.forEach { notification ->
                runCatching { markNotificationReadUseCase(notification.id) }
            }
            loadNotifications()
        }
    }
}
