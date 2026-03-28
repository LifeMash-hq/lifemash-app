package org.bmsk.lifemash.explore.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import org.bmsk.lifemash.explore.domain.model.ExploreMoment
import org.bmsk.lifemash.explore.domain.model.UserSummary
import org.bmsk.lifemash.explore.domain.repository.ExploreRepository

data class ExploreUiState(
    val query: String = "",
    val trending: List<UserSummary> = emptyList(),
    val trendingMoments: List<ExploreMoment> = emptyList(),
    val searchResults: List<UserSummary> = emptyList(),
    val recentSearches: List<String> = emptyList(),
    val isSearching: Boolean = false,
)

class ExploreViewModel(
    private val exploreRepository: ExploreRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ExploreUiState())
    val uiState: StateFlow<ExploreUiState> = _uiState

    init {
        loadTrending()
    }

    private fun loadTrending() {
        viewModelScope.launch {
            exploreRepository.getTrending()
                .catch { /* ignore */ }
                .collect { _uiState.value = _uiState.value.copy(trending = it) }
        }
        viewModelScope.launch {
            exploreRepository.getTrendingMoments()
                .catch { /* ignore */ }
                .collect { _uiState.value = _uiState.value.copy(trendingMoments = it) }
        }
    }

    fun search(query: String) {
        _uiState.value = _uiState.value.copy(query = query)
        if (query.isBlank()) {
            _uiState.value = _uiState.value.copy(searchResults = emptyList(), isSearching = false)
            return
        }
        _uiState.value = _uiState.value.copy(isSearching = true)
        viewModelScope.launch {
            exploreRepository.searchUsers(query)
                .catch { _uiState.value = _uiState.value.copy(isSearching = false) }
                .collect { results ->
                    val recent = (_uiState.value.recentSearches + query).distinct().takeLast(10)
                    _uiState.value = _uiState.value.copy(searchResults = results, isSearching = false, recentSearches = recent)
                }
        }
    }

    fun deleteRecentSearch(query: String) {
        _uiState.value = _uiState.value.copy(recentSearches = _uiState.value.recentSearches - query)
    }
}
