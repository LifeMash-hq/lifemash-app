package org.bmsk.lifemash.home.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.bmsk.lifemash.auth.domain.repository.AuthRepository
import org.bmsk.lifemash.home.api.BlocksTodayData
import org.bmsk.lifemash.home.api.HomeBlock
import org.bmsk.lifemash.home.domain.usecase.GetBlocksTodayUseCase
import org.bmsk.lifemash.home.domain.usecase.GetHomeLayoutUseCase
import org.bmsk.lifemash.home.domain.usecase.SaveHomeLayoutUseCase

class HomeViewModel(
    private val getLayout: GetHomeLayoutUseCase,
    private val saveLayout: SaveHomeLayoutUseCase,
    private val getTodayData: GetBlocksTodayUseCase,
    private val authRepository: AuthRepository,
) : ViewModel() {

    val blocks: StateFlow<List<HomeBlock>> = getLayout()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList(),
        )

    private val _todayData = MutableStateFlow<BlocksTodayData?>(null)
    val todayData: StateFlow<BlocksTodayData?> = _todayData.asStateFlow()

    private val _accessToken = MutableStateFlow<String?>(null)
    val accessToken: StateFlow<String?> = _accessToken.asStateFlow()

    internal fun loadInitialData() {
        viewModelScope.launch {
            runCatching { getTodayData() }
                .onSuccess { _todayData.value = it }
        }
        viewModelScope.launch {
            _accessToken.value = authRepository.getStoredToken()?.accessToken
        }
    }

    fun moveBlockUp(index: Int) {
        if (index <= 0) return
        val current = blocks.value.toMutableList()
        val temp = current[index - 1]
        current[index - 1] = current[index]
        current[index] = temp
        viewModelScope.launch { saveLayout(current) }
    }

    fun moveBlockDown(index: Int) {
        val current = blocks.value.toMutableList()
        if (index >= current.size - 1) return
        val temp = current[index + 1]
        current[index + 1] = current[index]
        current[index] = temp
        viewModelScope.launch { saveLayout(current) }
    }

    fun removeBlock(block: HomeBlock) {
        val current = blocks.value.filter { it.id != block.id }
        viewModelScope.launch { saveLayout(current) }
    }

    fun toggleVisibility(block: HomeBlock) {
        val current = blocks.value.map { b ->
            if (b.id == block.id) b.withVisible(!b.visible) else b
        }
        viewModelScope.launch { saveLayout(current) }
    }

    private fun HomeBlock.withVisible(visible: Boolean): HomeBlock = when (this) {
        is HomeBlock.CalendarToday -> copy(visible = visible)
        is HomeBlock.Groups -> copy(visible = visible)
        is HomeBlock.Assistant -> copy(visible = visible)
        is HomeBlock.WebViewBlock -> copy(visible = visible)
    }
}
