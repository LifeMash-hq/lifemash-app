package org.bmsk.lifemash.memo.impl

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.bmsk.lifemash.domain.calendar.Group
import org.bmsk.lifemash.domain.calendar.GroupType
import org.bmsk.lifemash.domain.calendar.GroupRepository
import org.bmsk.lifemash.domain.memo.ChecklistItem
import org.bmsk.lifemash.domain.memo.Memo
import org.bmsk.lifemash.domain.memo.MemoRepository
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalTime::class)
class MemoViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val now = Clock.System.now()

    private val testGroup = Group(
        id = "group-1",
        name = "테스트 그룹",
        type = GroupType.FRIENDS,
        maxMembers = 5,
        inviteCode = "CODE01",
        createdAt = now,
    )

    private val testMemo = Memo(
        id = "memo-1",
        groupId = "group-1",
        authorId = "user-1",
        title = "테스트 메모",
        content = "내용",
        isPinned = false,
        isChecklist = false,
        checklistItems = emptyList(),
        createdAt = now,
        updatedAt = now,
    )

    private val checklistMemo = Memo(
        id = "memo-2",
        groupId = "group-1",
        authorId = "user-1",
        title = "체크리스트",
        content = "",
        isPinned = false,
        isChecklist = true,
        checklistItems = listOf(
            ChecklistItem(id = "item-1", content = "할 일 1", isChecked = false, sortOrder = 0),
            ChecklistItem(id = "item-2", content = "할 일 2", isChecked = true, sortOrder = 1),
        ),
        createdAt = now,
        updatedAt = now,
    )

    private var memosResult: List<Memo> = listOf(testMemo)
    private var searchResult: List<Memo> = listOf(testMemo)
    private var groupsResult: List<Group> = listOf(testGroup)
    private var lastSyncedItems: List<ChecklistItem>? = null

    private val fakeMemoRepository = object : MemoRepository {
        override suspend fun getGroupMemos(groupId: String): List<Memo> = memosResult
        override suspend fun getMemo(groupId: String, memoId: String): Memo = testMemo
        override suspend fun createMemo(
            groupId: String,
            title: String,
            content: String,
            isPinned: Boolean,
            isChecklist: Boolean,
            checklistItems: List<ChecklistItem>,
        ): Memo = Memo(
            id = "new-memo",
            groupId = groupId,
            authorId = "user-1",
            title = title,
            content = content,
            isPinned = isPinned,
            isChecklist = isChecklist,
            checklistItems = checklistItems,
            createdAt = now,
            updatedAt = now,
        ).also { memosResult = memosResult + it }
        override suspend fun updateMemo(
            groupId: String,
            memoId: String,
            title: String?,
            content: String?,
            isPinned: Boolean?,
        ): Memo = testMemo.copy(
            title = title ?: testMemo.title,
            isPinned = isPinned ?: testMemo.isPinned,
        )
        override suspend fun deleteMemo(groupId: String, memoId: String) {
            memosResult = memosResult.filter { it.id != memoId }
        }
        override suspend fun searchMemos(groupId: String, query: String): List<Memo> = searchResult
        override suspend fun syncChecklist(
            groupId: String,
            memoId: String,
            items: List<ChecklistItem>,
        ): List<ChecklistItem> {
            lastSyncedItems = items
            return items
        }
    }

    private val fakeGroupRepository = object : GroupRepository {
        override suspend fun createGroup(type: GroupType, name: String?): Group = testGroup
        override suspend fun joinGroup(inviteCode: String): Group = testGroup
        override suspend fun getMyGroups(): List<Group> = groupsResult
        override suspend fun getGroup(groupId: String): Group = testGroup
        override suspend fun deleteGroup(groupId: String) {}
        override suspend fun updateGroupName(groupId: String, name: String): Group = testGroup
    }

    private fun createViewModel() = MemoViewModel(
        memoRepository = fakeMemoRepository,
        groupRepository = fakeGroupRepository,
    )

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        memosResult = listOf(testMemo)
        lastSyncedItems = null
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ─── loadMemos ─────────────────────────────────────────────────────────────

    @Test
    fun `loadMemos 성공 시 memos가 로드된다`() = runTest(testDispatcher) {
        val viewModel = createViewModel()

        viewModel.loadMemos()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(1, state.memos.size)
        assertEquals("memo-1", state.memos[0].id)
    }

    @Test
    fun `그룹이 없으면 memos가 빈 채로 유지된다`() = runTest(testDispatcher) {
        groupsResult = emptyList()
        val viewModel = createViewModel()

        viewModel.loadMemos()

        assertEquals(0, viewModel.uiState.value.memos.size)
    }

    // ─── createMemo ────────────────────────────────────────────────────────────

    @Test
    fun `createMemo 성공 시 memos가 갱신된다`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        viewModel.loadMemos()

        viewModel.createMemo(
            "새 메모",
            "내용",
            isPinned = false,
            isChecklist = false,
        )

        val state = viewModel.uiState.value
        assertFalse(state.isCreating)
        assertEquals(MemoOverlay.None, state.overlay)
        assertEquals(2, state.memos.size)
    }

    // ─── deleteMemo ────────────────────────────────────────────────────────────

    @Test
    fun `deleteMemo 성공 시 해당 메모가 목록에서 제거된다`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        viewModel.loadMemos()

        viewModel.deleteMemo("memo-1")

        assertEquals(0, viewModel.uiState.value.memos.size)
        assertEquals(MemoOverlay.None, viewModel.uiState.value.overlay)
    }

    // ─── search ────────────────────────────────────────────────────────────────

    @Test
    fun `search 빈 쿼리이면 getGroupMemos를 호출한다`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        viewModel.loadMemos()

        viewModel.search("")

        assertEquals("", viewModel.uiState.value.searchQuery)
        assertEquals(1, viewModel.uiState.value.memos.size)
    }

    @Test
    fun `search 키워드 입력 시 searchMemos 결과가 표시된다`() = runTest(testDispatcher) {
        searchResult = listOf(testMemo.copy(title = "검색된 메모"))
        val viewModel = createViewModel()
        viewModel.loadMemos()

        viewModel.search("검색어")

        val state = viewModel.uiState.value
        assertEquals("검색어", state.searchQuery)
        assertEquals("검색된 메모", state.memos[0].title)
    }

    // ─── toggleChecklistItem ───────────────────────────────────────────────────

    @Test
    fun `toggleChecklistItem 호출 시 해당 아이템의 isChecked가 토글된다`() = runTest(testDispatcher) {
        memosResult = listOf(checklistMemo)
        val viewModel = createViewModel()
        viewModel.loadMemos()

        viewModel.toggleChecklistItem("memo-2", "item-1")

        val memo = viewModel.uiState.value.memos.first { it.id == "memo-2" }
        assertTrue(memo.checklistItems.first { it.id == "item-1" }.isChecked)
    }

    @Test
    fun `toggleChecklistItem은 서버 syncChecklist를 호출한다`() = runTest(testDispatcher) {
        memosResult = listOf(checklistMemo)
        val viewModel = createViewModel()
        viewModel.loadMemos()

        viewModel.toggleChecklistItem("memo-2", "item-1")

        assertNotNull(lastSyncedItems)
        assertTrue(lastSyncedItems!!.first { it.id == "item-1" }.isChecked)
    }

    @Test
    fun `toggleChecklistItem은 Detail overlay의 memo도 업데이트한다`() = runTest(testDispatcher) {
        memosResult = listOf(checklistMemo)
        val viewModel = createViewModel()
        viewModel.loadMemos()
        viewModel.showOverlay(MemoOverlay.Detail(checklistMemo))

        viewModel.toggleChecklistItem("memo-2", "item-2") // isChecked=true → false

        val overlay = viewModel.uiState.value.overlay
        assertIs<MemoOverlay.Detail>(overlay)
        assertFalse(overlay.memo.checklistItems.first { it.id == "item-2" }.isChecked)
    }

    // ─── overlay / error ───────────────────────────────────────────────────────

    @Test
    fun `showOverlay와 dismissOverlay가 overlay 상태를 변경한다`() = runTest(testDispatcher) {
        val viewModel = createViewModel()

        viewModel.showOverlay(MemoOverlay.Create())
        assertIs<MemoOverlay.Create>(viewModel.uiState.value.overlay)

        viewModel.dismissOverlay()
        assertEquals(MemoOverlay.None, viewModel.uiState.value.overlay)
    }

    @Test
    fun `clearError 호출 시 errorMessage가 null이 된다`() = runTest(testDispatcher) {
        groupsResult = emptyList()
        val failingGroupRepo = object : GroupRepository by fakeGroupRepository {
            override suspend fun getMyGroups(): List<Group> = throw RuntimeException("에러")
        }
        val viewModel = MemoViewModel(
            memoRepository = fakeMemoRepository,
            groupRepository = failingGroupRepo,
        )
        viewModel.loadMemos()
        assertNotNull(viewModel.uiState.value.errorMessage)

        viewModel.clearError()

        assertNull(viewModel.uiState.value.errorMessage)
    }
}
