package org.bmsk.lifemash.home.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.bmsk.lifemash.home.api.HomeBlock
import org.bmsk.lifemash.home.api.MarketplaceBlockInfo
import org.bmsk.lifemash.home.domain.usecase.GetHomeLayoutUseCase
import org.bmsk.lifemash.home.domain.usecase.GetMarketplaceBlocksUseCase
import org.bmsk.lifemash.home.domain.usecase.InstallMarketplaceBlockUseCase

class MarketplaceViewModel(
    private val getBlocks: GetMarketplaceBlocksUseCase,
    private val install: InstallMarketplaceBlockUseCase,
    private val getLayout: GetHomeLayoutUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<MarketplaceUiState>(MarketplaceUiState.Loading)
    val uiState: StateFlow<MarketplaceUiState> = _uiState

    val installedIds: StateFlow<Set<String>> = getLayout()
        .map { layout ->
            layout.filterIsInstance<HomeBlock.WebViewBlock>().map { it.blockId }.toSet()
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptySet())

    fun retry() {
        _uiState.value = MarketplaceUiState.Loading
        loadBlocks()
    }

    internal fun loadBlocks() {
        viewModelScope.launch {
            runCatching { getBlocks() }
                .onSuccess { _uiState.value = MarketplaceUiState.Loaded(blocks = it) }
                .onFailure { e ->
                    println("[MarketplaceViewModel] 마켓플레이스 블록 로드 실패: $e")
                    e.printStackTrace()
                    _uiState.value = MarketplaceUiState.Error(
                        e.message ?: e::class.simpleName ?: "알 수 없는 오류",
                    )
                }
        }
    }

    fun installBlock(block: MarketplaceBlockInfo) {
        viewModelScope.launch {
            updateLoaded { copy(installing = block.id) }
            runCatching { install(block) }
                .onFailure { e ->
                    println("[MarketplaceViewModel] 블록 설치 실패 (${block.id}): $e")
                    e.printStackTrace()
                    _uiState.value = MarketplaceUiState.Error(
                        e.message ?: e::class.simpleName ?: "설치 실패",
                    )
                }
            updateLoaded { copy(installing = null) }
        }
    }

    private fun updateLoaded(transform: MarketplaceUiState.Loaded.() -> MarketplaceUiState.Loaded) {
        _uiState.update { state ->
            when (state) {
                is MarketplaceUiState.Loaded -> state.transform()
                else -> state
            }
        }
    }
}
