package org.bmsk.lifemash.feature.feed

import android.util.Log
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
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.bmsk.lifemash.domain.core.model.Article
import org.bmsk.lifemash.domain.core.model.ArticleCategory
import org.bmsk.lifemash.domain.core.model.ArticleId
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
    private val originalArticles = mutableMapOf<ArticleId, Article>()

    private val scrappedArticleIds: StateFlow<Set<ArticleId>> = getScrappedArticleIdsUseCase()
        .onEach { Log.e("FeedViewModel", "scrappedArticleIds: $it") }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptySet()
        )

    val uiState: StateFlow<FeedUiState> = combine(
        _uiState,
        scrappedArticleIds
    ) { state, scrappedIds ->
        val updatedArticlesById = state.articlesById
            .mapValues { (_, article) ->
                val newIsScrapped = article.id in scrappedIds
                if (article.isScrapped != newIsScrapped) article.copy(isScrapped = newIsScrapped)
                else article
            }
            .toPersistentMap()

        val newState = state.copy(articlesById = updatedArticlesById)
        newState.copy(visibleArticles = recalculateVisibleArticles(newState))
    }
        .onEach { s ->
            val idsInState = s.articlesById.keys
            val inter = idsInState.intersect(scrappedArticleIds.value)
            Log.e("FeedViewModel", "idsInState=${idsInState.size}, scrapped=${scrappedArticleIds.value.size}, inter=${inter.size}")

            // 샘플 몇 개 비교해서 차이 확인
            idsInState.take(5).forEach { id ->
                val hit = scrappedArticleIds.value.contains(id)
                if (!hit) {
                    Log.e("FeedViewModel", "mismatch sample: stateId=${id.value}")
                }
            }
        }
        .stateIn(
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
                originalArticles.putAll(articles.associateBy(Article::id))

                val articleUis = articles
                    .map { ArticleUi.from(it, isScrapped = it.id in scrappedArticleIds.value) }
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

    fun scrapArticle(article: ArticleUi) {
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
