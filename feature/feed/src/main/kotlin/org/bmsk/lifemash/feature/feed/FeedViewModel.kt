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
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
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
     * UI에 최종적으로 노출되는 StateFlow입니다.
     * ViewModel의 내부 상태(_internalState)와 스크랩된 기사 ID 목록(scrappedArticleIds)을
     * 결합(combine)하여 최종 UI 상태를 생성합니다.
     *
     * 스크랩 상태가 변경되면, 이 Flow가 자동으로 새로운 UI 상태를 발행하여 UI를 업데이트합니다.
     */
    val uiState: StateFlow<FeedUiState> = combine(
        _internalState,
        scrappedArticleIds
    ) { state, scrappedIds ->
        val updatedArticlesById = state.articlesById
            .mapValues { (_, articleState) ->
                val newIsScrapped = articleState.article.id in scrappedIds
                if (articleState.isScrapped != newIsScrapped) {
                    articleState.copy(isScrapped = newIsScrapped)
                } else {
                    articleState
                }
            }
            .toPersistentMap()

        val newState = state.copy(articlesById = updatedArticlesById)
        newState.copy(visibleArticles = recalculateVisibleArticles(newState))
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = FeedUiState.Initial
        )

    /**
     * 특정 카테고리의 기사를 가져옵니다.
     * UseCase를 통해 기사를 가져온 후, 스크랩 상태를 반영하여 UI 모델로 변환하고 내부 상태를 업데이트합니다.
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
                    .map { ArticleUiState.from(it, isScrapped = it.id in scrappedArticleIds.value) }
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
     * 기사의 현재 스크랩 상태에 따라 add 또는 delete UseCase를 실행합니다.
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

    fun setQueryText(queryText: String) {
        _internalState.update { it.copy(queryText = queryText) }
    }

    fun setSearchMode(isSearchMode: Boolean) {
        _internalState.update { it.copy(isSearchMode = isSearchMode) }
    }

    fun setCategory(category: ArticleCategory) {
        _internalState.update { it.copy(selectedCategory = category) }
    }

    /**
     * 현재 선택된 카테고리에 따라 화면에 표시될 기사 목록을 계산
     */
    private fun recalculateVisibleArticles(state: FeedUiState): PersistentList<ArticleUiState> {
        val articles = if (state.selectedCategory == ArticleCategory.ALL) {
            ArticleCategory.entries.asSequence()
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