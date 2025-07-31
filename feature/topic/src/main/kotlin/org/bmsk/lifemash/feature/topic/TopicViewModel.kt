package org.bmsk.lifemash.feature.topic

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.bmsk.lifemash.core.model.NewsModel
import org.bmsk.lifemash.core.model.section.SBSSection
import org.bmsk.lifemash.feature.topic.usecase.GetGoogleNewsUseCase
import org.bmsk.lifemash.feature.topic.usecase.GetNewsImageUrlUseCase
import org.bmsk.lifemash.feature.topic.usecase.GetSBSNewsUseCase
import org.bmsk.lifemash.feature.topic.usecase.ScrapNewsUseCase
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

@HiltViewModel
internal class TopicViewModel @Inject constructor(
    private val getSBSNewsUseCase: GetSBSNewsUseCase,
    private val getGoogleNewsUseCase: GetGoogleNewsUseCase,
    private val scrapNewsUseCase: ScrapNewsUseCase,
    private val getNewsImageUrlUseCase: GetNewsImageUrlUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(TopicUiState())
    val uiState = _uiState.asStateFlow()

    private var lastAwaitJob: Job? = null

    fun setSelectedOverflowMenuNews(newsModel: NewsModel?) {
        _uiState.update { it.copy(selectedOverflowMenuNews = newsModel) }
    }

    fun setSection(section: SBSSection) {
        _uiState.update { it.copy(selectedSection = section) }
    }

    fun getNews(section: SBSSection) {
        viewModelScope.awaitAndLaunch {
            runCatching {
                // 이 시점에 이미 뉴스 로딩이 끝났을 수 있음
                val currentNewsLoadUiState = _uiState.value.getNewsLoadUiState(section)
                if (currentNewsLoadUiState is NewsLoadUiState.Loaded) {
                    return@awaitAndLaunch
                }

                // 뉴스 로딩 중으로 변경함
                val currentSectionStates = _uiState.value.sectionStates
                _uiState.update { state ->
                    state.copy(
                        sectionStates = currentSectionStates.map { sectionState ->
                            if (sectionState.section == section) {
                                sectionState.copy(newsLoadUiState = NewsLoadUiState.Loading)
                            } else {
                                sectionState
                            }
                        }
                    )
                }

                val newsModels = getSBSNewsUseCase(section)
                val selectedSection0 = _uiState.value.selectedSection
                val newsLoadUiState0 = NewsLoadUiState.Loaded(newsModels)
                _uiState.update { it.setNewsLoadUiState(selectedSection0, newsLoadUiState0) }

                val imageUrlUpdatedNewsModels = updateNewsImageUrl(newsModels)
                val selectedSection1 = _uiState.value.selectedSection
                val newsLoadUiState1 = NewsLoadUiState.Loaded(imageUrlUpdatedNewsModels)
                _uiState.update { it.setNewsLoadUiState(selectedSection1, newsLoadUiState1) }
            }.onFailure { t ->
                val newsLoadUiState = NewsLoadUiState.Error(t)
                val selectedSection = _uiState.value.selectedSection
                _uiState.update { it.setNewsLoadUiState(selectedSection, newsLoadUiState) }
            }
        }
    }

    fun setQuery(query: String) {
        _uiState.update { it.copy(query = query) }
    }

    fun getGoogleNews(query: String) {
        viewModelScope.launch {
            runCatching {
                getGoogleNewsUseCase(query)
            }.onSuccess { newsModels ->
                _uiState.update { it.copy(googleNewsModels = newsModels) }
            }.onFailure { t ->
                _uiState.update { it.copy(searchErrorEvent = t) }
            }
        }
    }

    fun scrapNews(newsModel: NewsModel) {
        viewModelScope.launch {
            _uiState.update { it.copy(scrapingUiState = ScrapingUiState.IsScraping) }
            runCatching {
                scrapNewsUseCase(newsModel)
            }.onFailure { t ->
                _uiState.update { it.copy(scrapingUiState = ScrapingUiState.ScrapingError(t)) }
            }.onSuccess {
                _uiState.update { it.copy(scrapingUiState = ScrapingUiState.ScrapCompleted) }
            }
        }
    }

    private suspend fun updateNewsImageUrl(newsModels: List<NewsModel>): List<NewsModel> {
        return coroutineScope {
            newsModels.map { newsModel ->
                async {
                    val imageUrl = getNewsImageUrlUseCase(newsModel.link).also { Log.e("ddd", it) }
                    newsModel.copy(imageUrl = imageUrl)
                }
            }.awaitAll()
        }
    }

    fun handleSearchErrorEvent() {
        _uiState.update { it.copy(searchErrorEvent = null) }
    }

    fun handleScrapErrorEvent() {
        _uiState.update { it.copy(scrapErrorEvent = null) }
    }

    fun setScrapingUiState(state: ScrapingUiState = ScrapingUiState.Idle) {
        _uiState.update { it.copy(scrapingUiState = state) }
    }

    private fun CoroutineScope.awaitAndLaunch(
        context: CoroutineContext = EmptyCoroutineContext,
        block: suspend CoroutineScope.() -> Unit,
    ) {
        val job = launch(
            context = context,
            start = CoroutineStart.LAZY,
            block = block,
        )
        with(lastAwaitJob) {
            if (this == null || this.isCompleted) {
                job.start()
            } else {
                job.invokeOnCompletion { job.start() }
            }

        }
        lastAwaitJob = job
    }
}