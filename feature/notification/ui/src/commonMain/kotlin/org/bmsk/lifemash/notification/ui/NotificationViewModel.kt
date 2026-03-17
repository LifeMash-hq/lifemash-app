package org.bmsk.lifemash.notification.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.bmsk.lifemash.notification.domain.model.Keyword
import org.bmsk.lifemash.notification.domain.usecase.AddKeywordUseCase
import org.bmsk.lifemash.notification.domain.usecase.GetKeywordsUseCase
import org.bmsk.lifemash.notification.domain.usecase.RemoveKeywordUseCase

internal class NotificationViewModel(
    getKeywordsUseCase: GetKeywordsUseCase,
    private val addKeywordUseCase: AddKeywordUseCase,
    private val removeKeywordUseCase: RemoveKeywordUseCase,
) : ViewModel() {

    val uiState: StateFlow<NotificationUiState> = getKeywordsUseCase()
        .map { keywords ->
            if (keywords.isEmpty()) NotificationUiState.Empty
            else NotificationUiState.Loaded(keywords.toPersistentList())
        }
        .onStart { emit(NotificationUiState.Loading) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), NotificationUiState.Loading)

    fun addKeyword(keyword: String) {
        runCatching { Keyword.from(keyword) }.onSuccess {
            viewModelScope.launch { addKeywordUseCase(keyword) }
        }
    }

    fun removeKeyword(id: Long) {
        viewModelScope.launch { removeKeywordUseCase(id) }
    }
}
