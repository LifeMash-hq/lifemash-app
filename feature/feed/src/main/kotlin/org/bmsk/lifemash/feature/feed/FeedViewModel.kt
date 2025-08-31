package org.bmsk.lifemash.feature.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.bmsk.lifemash.domain.core.model.Article
import org.bmsk.lifemash.domain.core.model.ArticleCategory
import org.bmsk.lifemash.domain.feed.usecase.GetArticlesUseCase
import org.bmsk.lifemash.domain.scrap.usecase.AddScrapUseCase
import org.bmsk.lifemash.domain.scrap.usecase.DeleteScrappedArticleUseCase
import org.bmsk.lifemash.domain.scrap.usecase.GetScrappedArticleIdsUseCase
import javax.inject.Inject

@HiltViewModel
internal class FeedViewModel @Inject constructor(
    private val getArticlesUseCase: GetArticlesUseCase,
    getScrappedArticleIdsUseCase: GetScrappedArticleIdsUseCase,
    private val addScrapUseCase: AddScrapUseCase,
    private val deleteScrappedArticleUseCase: DeleteScrappedArticleUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(FeedUiState.Initial)
    private val originalArticles = mutableMapOf<String, Article>()

    private val scrappedArticleIds: StateFlow<Set<String>> = getScrappedArticleIdsUseCase()
        .map { ids -> ids.map { it.value }.toSet() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptySet()
        )

    val uiState: StateFlow<FeedUiState> = combine(
        _uiState,
        scrappedArticleIds
    ) { state, scrappedIds ->
        val newArticlesById = state.articlesById.mapValues {
            val article = it.value
            article.copy(isScrapped = article.id in scrappedIds)
        }.toPersistentMap()

        val newState = state.copy(articlesById = newArticlesById)
        newState.copy(visibleArticles = recalculateVisibleArticles(newState))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = FeedUiState.Initial
    )

    fun getArticles(category: ArticleCategory) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    loadStateByCategory = it.loadStateByCategory.put(
                        category,
                        LoadState.Loading
                    )
                )
            }

            runCatching {
                getArticlesUseCase(category)
            }.onSuccess { articles ->
                originalArticles.putAll(articles.associateBy { it.id.value })

                val articleUis = articles
                    .map { ArticleUi.from(it, isScrapped = it.id.value in scrappedArticleIds.value) }
                    .toPersistentList()

                _uiState.update { currentState ->
                    val mergedArticlesById = currentState
                        .articlesById
                        .putAll(
                            m = articleUis
                                .associateBy(ArticleUi::id)
                                .toPersistentMap()
                        )
                    val newIds = articleUis.map { it.id }.toPersistentList()

                    currentState.copy(
                        articlesById = mergedArticlesById,
                        idsByCategory = currentState.idsByCategory.put(category, newIds),
                        loadStateByCategory = currentState.loadStateByCategory.put(
                            category,
                            LoadState.Loaded
                        )
                    )
                }
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        loadStateByCategory = it.loadStateByCategory.put(
                            category,
                            LoadState.Error(throwable)
                        )
                    )
                }
            }
        }
    }

    fun onScrapClick(article: ArticleUi) {
        val originalArticle = originalArticles[article.id] ?: return

        viewModelScope.launch {
            if (article.isScrapped) {
                deleteScrappedArticleUseCase(originalArticle.id)
            } else {
                addScrapUseCase(originalArticle)
            }
        }
    }

    fun setQueryText(queryText: String) {
        _uiState.update { it.copy(queryText = queryText) }
    }

    fun setSearchMode(isSearchMode: Boolean) {
        _uiState.update { it.copy(isSearchMode = isSearchMode) }
    }

    fun setCategory(category: ArticleCategory) {
        _uiState.update { currentState ->
            val newState = currentState.copy(selectedCategory = category)
            newState.copy(visibleArticles = recalculateVisibleArticles(newState))
        }
    }

    private fun recalculateVisibleArticles(state: FeedUiState): PersistentList<ArticleUi> {
        val articles = if (state.selectedCategory == ArticleCategory.ALL) {
            ArticleCategory.entries.asSequence()
                .flatMap { (state.idsByCategory[it] ?: persistentListOf()).asSequence() }
                .distinct()
                .mapNotNull(state.articlesById::get)
        } else {
            (state.idsByCategory[state.selectedCategory] ?: persistentListOf()).asSequence()
                .mapNotNull(state.articlesById::get)
        }
        return articles.sortedByDescending { it.publishedAtInstant }.toPersistentList()
    }
}
