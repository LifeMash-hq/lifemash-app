package org.bmsk.lifemash.fake

import org.bmsk.lifemash.memo.MemoRepository
import org.bmsk.lifemash.model.memo.CreateMemoRequest
import org.bmsk.lifemash.model.memo.MemoDto
import org.bmsk.lifemash.model.memo.UpdateMemoRequest
import kotlin.time.Clock
import kotlin.uuid.Uuid

class FakeMemoRepository : MemoRepository {
    private val memos = mutableListOf<MemoDto>()

    override fun findByGroup(groupId: Uuid): List<MemoDto> =
        memos.filter { it.groupId == groupId.toString() }
            .sortedWith(compareByDescending<MemoDto> { it.isPinned }.thenByDescending { it.updatedAt })

    override fun findById(memoId: Uuid): MemoDto? =
        memos.find { it.id == memoId.toString() }

    override fun create(groupId: Uuid, authorId: Uuid, request: CreateMemoRequest): MemoDto {
        val now = Clock.System.now()
        val memo = MemoDto(
            id = Uuid.random().toString(),
            groupId = groupId.toString(),
            authorId = authorId.toString(),
            title = request.title,
            content = request.content,
            isPinned = request.isPinned,
            isChecklist = request.isChecklist,
            checklistItems = emptyList(),
            createdAt = now,
            updatedAt = now,
        )
        memos.add(memo)
        return memo
    }

    override fun update(memoId: Uuid, request: UpdateMemoRequest): MemoDto {
        val idx = memos.indexOfFirst { it.id == memoId.toString() }
        val existing = memos[idx]
        val updated = existing.copy(
            title = request.title ?: existing.title,
            content = request.content ?: existing.content,
            isPinned = request.isPinned ?: existing.isPinned,
            updatedAt = Clock.System.now(),
        )
        memos[idx] = updated
        return updated
    }

    override fun delete(memoId: Uuid) {
        memos.removeAll { it.id == memoId.toString() }
    }

    override fun search(groupId: Uuid, query: String): List<MemoDto> {
        val lower = query.lowercase()
        return memos.filter {
            it.groupId == groupId.toString() &&
                (it.title.lowercase().contains(lower) || it.content.lowercase().contains(lower))
        }
    }

    override fun countPinned(groupId: Uuid): Int =
        memos.count { it.groupId == groupId.toString() && it.isPinned }
}
