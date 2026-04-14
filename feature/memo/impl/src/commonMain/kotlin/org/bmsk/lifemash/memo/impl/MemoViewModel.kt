package org.bmsk.lifemash.memo.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.bmsk.lifemash.domain.usecase.calendar.GetMyGroupsUseCase
import org.bmsk.lifemash.domain.usecase.memo.CreateMemoUseCase
import org.bmsk.lifemash.domain.usecase.memo.DeleteMemoUseCase
import org.bmsk.lifemash.domain.usecase.memo.GetGroupMemosUseCase
import org.bmsk.lifemash.domain.usecase.memo.SearchMemosUseCase
import org.bmsk.lifemash.domain.usecase.memo.SyncChecklistUseCase
import org.bmsk.lifemash.domain.usecase.memo.UpdateMemoUseCase

internal class MemoViewModel(
    private val getMyGroups: GetMyGroupsUseCase,
    private val getGroupMemos: GetGroupMemosUseCase,
    private val createMemo: CreateMemoUseCase,
    private val updateMemo: UpdateMemoUseCase,
    private val deleteMemo: DeleteMemoUseCase,
    private val searchMemos: SearchMemosUseCase,
    private val syncChecklist: SyncChecklistUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MemoUiState.Default)
    val uiState: StateFlow<MemoUiState> = _uiState.asStateFlow()

    private var groupId: String? = null

    fun loadMemos() {
        viewModelScope.launch {
            runCatching {
                val groups = getMyGroups()
                val firstGroup = groups.firstOrNull() ?: return@runCatching
                groupId = firstGroup.id
                val memos = getGroupMemos(firstGroup.id)
                _uiState.update {
                    it.copy(isLoading = false, memos = memos.toPersistentList())
                }
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message ?: "메모 로드 실패") }
            }
        }
    }

    fun createMemo(
        title: String,
        content: String,
        isPinned: Boolean,
        isChecklist: Boolean,
    ) {
        val gId = groupId ?: return
        _uiState.update { it.copy(isCreating = true) }
        viewModelScope.launch {
            runCatching {
                createMemo(
                    groupId = gId,
                    title = title,
                    content = content,
                    isPinned = isPinned,
                    isChecklist = isChecklist,
                    checklistItems = emptyList(),
                )
                val memos = getGroupMemos(gId)
                _uiState.update {
                    it.copy(
                        isCreating = false,
                        overlay = MemoOverlay.None,
                        memos = memos.toPersistentList(),
                    )
                }
            }.onFailure { e ->
                _uiState.update { it.copy(isCreating = false, errorMessage = e.message ?: "메모 생성 실패") }
            }
        }
    }

    fun updateMemo(
        memoId: String,
        title: String?,
        content: String?,
        isPinned: Boolean?,
    ) {
        val gId = groupId ?: return
        _uiState.update { it.copy(isUpdating = true) }
        viewModelScope.launch {
            runCatching {
                updateMemo(gId, memoId, title, content, isPinned)
                val memos = getGroupMemos(gId)
                _uiState.update {
                    it.copy(
                        isUpdating = false,
                        overlay = MemoOverlay.None,
                        memos = memos.toPersistentList(),
                    )
                }
            }.onFailure { e ->
                _uiState.update { it.copy(isUpdating = false, errorMessage = e.message ?: "메모 수정 실패") }
            }
        }
    }

    fun deleteMemo(memoId: String) {
        val gId = groupId ?: return
        viewModelScope.launch {
            runCatching {
                deleteMemo(gId, memoId)
                val memos = getGroupMemos(gId)
                _uiState.update {
                    it.copy(overlay = MemoOverlay.None, memos = memos.toPersistentList())
                }
            }.onFailure { e ->
                _uiState.update { it.copy(errorMessage = e.message ?: "메모 삭제 실패") }
            }
        }
    }

    fun search(query: String) {
        val gId = groupId ?: return
        _uiState.update { it.copy(searchQuery = query) }
        if (query.isBlank()) {
            viewModelScope.launch {
                runCatching {
                    val memos = getGroupMemos(gId)
                    _uiState.update { it.copy(memos = memos.toPersistentList()) }
                }.onFailure { e ->
                    _uiState.update { it.copy(errorMessage = e.message ?: "메모 로드 실패") }
                }
            }
            return
        }
        viewModelScope.launch {
            runCatching {
                val memos = searchMemos(gId, query)
                _uiState.update { it.copy(memos = memos.toPersistentList()) }
            }.onFailure { e ->
                _uiState.update { it.copy(errorMessage = e.message ?: "검색 실패") }
            }
        }
    }

    fun toggleChecklistItem(memoId: String, itemId: String) {
        val gId = groupId ?: return
        val memo = _uiState.value.memos.find { it.id == memoId } ?: return
        val updatedItems = memo.checklistItems.map { item ->
            if (item.id == itemId) item.copy(isChecked = !item.isChecked) else item
        }
        viewModelScope.launch {
            runCatching {
                val synced = syncChecklist(gId, memoId, updatedItems)
                val memos = _uiState.value.memos.map { m ->
                    if (m.id == memoId) m.copy(checklistItems = synced) else m
                }.toPersistentList()
                val overlay = _uiState.value.overlay
                val updatedOverlay = if (overlay is MemoOverlay.Detail && overlay.memo.id == memoId) {
                    MemoOverlay.Detail(overlay.memo.copy(checklistItems = synced))
                } else overlay
                _uiState.update { it.copy(memos = memos, overlay = updatedOverlay) }
            }.onFailure { e ->
                _uiState.update { it.copy(errorMessage = e.message ?: "체크리스트 업데이트 실패") }
            }
        }
    }

    fun showOverlay(overlay: MemoOverlay) {
        _uiState.update { it.copy(overlay = overlay) }
    }

    fun dismissOverlay() {
        _uiState.update { it.copy(overlay = MemoOverlay.None) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
