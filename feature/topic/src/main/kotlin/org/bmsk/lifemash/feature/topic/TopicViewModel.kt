package org.bmsk.lifemash.feature.topic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.bmsk.lifemash.core.model.NewsModel
import org.bmsk.lifemash.core.model.section.SBSSection
import org.bmsk.lifemash.feature.topic.usecase.GetGoogleNewsUseCase
import org.bmsk.lifemash.feature.topic.usecase.GetNewsWithImagesUseCase
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
    private val getNewsWIthImagesUrlUseCase: GetNewsWithImagesUseCase
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
                _uiState.update { state ->
                    val newsLoadUiState0 = NewsLoadUiState.Loaded(newsModels)
                    state.setNewsLoadUiState(section, newsLoadUiState0)
                }

                val imageUrlUpdatedNewsModels = getNewsWIthImagesUrlUseCase(newsModels)
                _uiState.update { state ->
                    val newsLoadUiState1 = NewsLoadUiState.Loaded(imageUrlUpdatedNewsModels)
                    state.setNewsLoadUiState(section, newsLoadUiState1)
                }
            }.onFailure { t ->
                val newsLoadUiState = NewsLoadUiState.Error(t)
                _uiState.update { it.setNewsLoadUiState(section, newsLoadUiState) }
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
                this.invokeOnCompletion { job.start() }
            }
        }
        lastAwaitJob = job
    }
}