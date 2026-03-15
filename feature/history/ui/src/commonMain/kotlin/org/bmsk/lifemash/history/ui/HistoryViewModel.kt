package org.bmsk.lifemash.history.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import org.bmsk.lifemash.feed.domain.history.usecase.GetReadingHistoryUseCase
class HistoryViewModel(
    getReadingHistoryUseCase: GetReadingHistoryUseCase,
) : ViewModel() {

    val uiState: StateFlow<HistoryUiState> = getReadingHistoryUseCase()
        .map { articles ->
            if (articles.isEmpty()) {
                HistoryUiState.Empty
            } else {
                HistoryUiState.Loaded(articles.map { HistoryArticleUi.from(it) }.toPersistentList())
            }
        }
        .onStart { emit(HistoryUiState.Loading) }
        .catch { emit(HistoryUiState.Error(it)) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = HistoryUiState.Loading,
        )
}
