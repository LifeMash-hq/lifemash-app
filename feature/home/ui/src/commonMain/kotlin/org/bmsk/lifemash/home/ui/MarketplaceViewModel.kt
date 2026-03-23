package org.bmsk.lifemash.home.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
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

    private val _blocks = MutableStateFlow<List<MarketplaceBlockInfo>>(emptyList())
    val blocks: StateFlow<List<MarketplaceBlockInfo>> = _blocks

    val installedIds: StateFlow<Set<String>> = getLayout()
        .map { layout ->
            layout.filterIsInstance<HomeBlock.WebViewBlock>().map { it.blockId }.toSet()
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptySet())

    private val _installing = MutableStateFlow<String?>(null)
    val installing: StateFlow<String?> = _installing

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        loadBlocks()
    }

    fun retry() {
        _error.value = null
        loadBlocks()
    }

    private fun loadBlocks() {
        viewModelScope.launch {
            runCatching { getBlocks() }
                .onSuccess { _blocks.value = it }
                .onFailure {
                    println("[MarketplaceViewModel] 마켓플레이스 블록 로드 실패: $it")
                    it.printStackTrace()
                    _error.value = it.message ?: it::class.simpleName ?: "알 수 없는 오류"
                }
        }
    }

    fun installBlock(block: MarketplaceBlockInfo) {
        viewModelScope.launch {
            _installing.value = block.id
            runCatching { install(block) }
                .onFailure {
                    println("[MarketplaceViewModel] 블록 설치 실패 (${block.id}): $it")
                    it.printStackTrace()
                    _error.value = it.message ?: it::class.simpleName ?: "설치 실패"
                }
            _installing.value = null
        }
    }

}
