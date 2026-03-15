package org.bmsk.lifemash.feed.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.bmsk.lifemash.model.Article
import org.bmsk.lifemash.model.ArticleCategory
import org.bmsk.lifemash.model.ArticleId
import org.bmsk.lifemash.feed.domain.usecase.GetArticlesUseCase
import org.bmsk.lifemash.feed.domain.usecase.SearchArticlesUseCase
import org.bmsk.lifemash.feed.domain.history.usecase.AddToHistoryUseCase
import org.bmsk.lifemash.feed.domain.history.usecase.GetReadArticleIdsUseCase
import org.bmsk.lifemash.feed.domain.subscription.usecase.GetSubscribedCategoriesUseCase
import org.bmsk.lifemash.feed.domain.subscription.usecase.SetSubscribedCategoriesUseCase
import org.bmsk.lifemash.scrap.domain.usecase.AddScrapUseCase
import org.bmsk.lifemash.scrap.domain.usecase.DeleteScrappedArticleUseCase
import org.bmsk.lifemash.scrap.domain.usecase.GetScrappedArticleIdsUseCase
class FeedViewModel(
    private val getArticlesUseCase: GetArticlesUseCase,
    getScrappedArticleIdsUseCase: GetScrappedArticleIdsUseCase,
    private val addScrapUseCase: AddScrapUseCase,
    private val deleteScrappedArticleUseCase: DeleteScrappedArticleUseCase,
    getReadArticleIdsUseCase: GetReadArticleIdsUseCase,
    private val addToHistoryUseCase: AddToHistoryUseCase,
    private val searchArticlesUseCase: SearchArticlesUseCase,
    getSubscribedCategoriesUseCase: GetSubscribedCategoriesUseCase,
    private val setSubscribedCategoriesUseCase: SetSubscribedCategoriesUseCase,
) : ViewModel() {

    // region State

    /**
     * ViewModel의 내부 상태를 관리하는 private MutableStateFlow입니다.
     * 주로 기사 목록, 카테고리별 로드 상태 등 UI와 직접 관련 없는 데이터를 다룹니다.
     */
    private val _internalState = MutableStateFlow(FeedUiState.Initial)

    /**
     * 스크랩된 모든 기사의 ID를 실시간으로 관찰하는 StateFlow입니다.
     * 데이터베이스가 변경되면 이 Flow가 새로운 ID Set을 발행합니다.
     */
    private val scrappedArticleIds: StateFlow<Set<ArticleId>> = getScrappedArticleIdsUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptySet()
        )

    /**
     * 읽은 기사의 ID를 실시간으로 관찰하는 StateFlow입니다.
     */
    private val readArticleIds: StateFlow<Set<ArticleId>> = getReadArticleIdsUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptySet()
        )

    /**
     * 구독 카테고리를 실시간으로 관찰하는 StateFlow입니다.
     */
    private val subscribedCategories: StateFlow<Set<ArticleCategory>> =
        getSubscribedCategoriesUseCase()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptySet(),
            )

    /**
     * UI에 최종적으로 노출되는 StateFlow입니다.
     */
    val uiState: StateFlow<FeedUiState> = combine(
        _internalState,
        scrappedArticleIds,
        readArticleIds,
        subscribedCategories,
    ) { state, scrappedIds, readIds, subscribedCats ->
        val updatedArticlesById = state.articlesById
            .mapValues { (_, articleState) ->
                val newIsScrapped = articleState.article.id in scrappedIds
                val newIsRead = articleState.article.id in readIds
                if (articleState.isScrapped != newIsScrapped || articleState.isRead != newIsRead) {
                    articleState.copy(isScrapped = newIsScrapped, isRead = newIsRead)
                } else {
                    articleState
                }
            }
            .toPersistentMap()

        val newState = state.copy(
            articlesById = updatedArticlesById,
            subscribedCategories = subscribedCats,
        )
        newState.copy(visibleArticles = recalculateVisibleArticles(newState))
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = FeedUiState.Initial
        )

    /**
     * 특정 카테고리의 기사를 가져옵니다.
     */
    fun getArticles(category: ArticleCategory) {
        viewModelScope.launch {
            _internalState.update { currentState ->
                val loadStateByCategory =
                    currentState.loadStateByCategory.put(category, LoadState.Loading)
                currentState.copy(loadStateByCategory = loadStateByCategory)
            }

            runCatching {
                getArticlesUseCase(category)
            }.onSuccess { articles ->
                val articleUiStates = articles
                    .map {
                        ArticleUiState.from(
                            it,
                            isScrapped = it.id in scrappedArticleIds.value,
                            isRead = it.id in readArticleIds.value,
                        )
                    }
                    .toPersistentList()

                _internalState.update { currentState ->
                    val mergedArticlesById = currentState
                        .articlesById
                        .putAll(
                            m = articleUiStates
                                .associateBy(ArticleUiState::id)
                                .toPersistentMap()
                        )
                    val newIds = articleUiStates.map { it.id }.toPersistentList()

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
                _internalState.update {
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

    /**
     * 사용자가 스크랩 버튼을 클릭했을 때 호출됩니다.
     */
    fun scrapArticle(state: ArticleUiState) {
        viewModelScope.launch {
            if (state.isScrapped) {
                deleteScrappedArticleUseCase(state.article.id)
            } else {
                addScrapUseCase(state.article)
            }
        }
    }

    fun addToHistory(article: Article) {
        viewModelScope.launch {
            addToHistoryUseCase(article)
        }
    }

    fun setQueryText(queryText: String) {
        _internalState.update { it.copy(queryText = queryText) }
    }

    fun searchArticles(query: String) {
        if (query.isBlank()) return
        viewModelScope.launch {
            runCatching {
                searchArticlesUseCase(query)
            }.onSuccess { articles ->
                val articleUiStates = articles.map {
                    ArticleUiState.from(
                        it,
                        isScrapped = it.id in scrappedArticleIds.value,
                        isRead = it.id in readArticleIds.value,
                    )
                }.toPersistentList()
                _internalState.update { it.copy(searchResults = articleUiStates) }
            }
        }
    }

    fun setSearchMode(isSearchMode: Boolean) {
        _internalState.update {
            if (isSearchMode) {
                it.copy(isSearchMode = true)
            } else {
                it.copy(isSearchMode = false, queryText = "", searchResults = persistentListOf())
            }
        }
    }

    fun setCategory(category: ArticleCategory) {
        _internalState.update { it.copy(selectedCategory = category) }
    }

    fun setSubscribedCategories(categories: Set<ArticleCategory>) {
        viewModelScope.launch { setSubscribedCategoriesUseCase(categories) }
    }

    /**
     * 현재 선택된 카테고리에 따라 화면에 표시될 기사 목록을 계산
     */
    private fun recalculateVisibleArticles(state: FeedUiState): PersistentList<ArticleUiState> {
        if (state.isSearchMode && state.searchResults.isNotEmpty()) {
            return state.searchResults
        }
        val articles = if (state.selectedCategory == ArticleCategory.ALL) {
            val targetCategories = if (state.subscribedCategories.isEmpty()) {
                ArticleCategory.entries
            } else {
                state.subscribedCategories.toList()
            }
            targetCategories.asSequence()
                .flatMap { (state.idsByCategory[it] ?: persistentListOf()).asSequence() }
                .distinct()
                .mapNotNull(state.articlesById::get)
        } else {
            (state.idsByCategory[state.selectedCategory] ?: persistentListOf()).asSequence()
                .mapNotNull(state.articlesById::get)
        }
        return articles.sortedByDescending { it.article.publishedAt }.toPersistentList()
    }
}
