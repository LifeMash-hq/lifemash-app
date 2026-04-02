package org.bmsk.lifemash.memo

import org.bmsk.lifemash.model.memo.CreateMemoRequest
import org.bmsk.lifemash.model.memo.MemoDto
import org.bmsk.lifemash.model.memo.UpdateMemoRequest
import kotlin.uuid.Uuid

interface MemoRepository {
    fun findByGroup(groupId: Uuid): List<MemoDto>
    fun findById(memoId: Uuid): MemoDto?
    fun create(groupId: Uuid, authorId: Uuid, request: CreateMemoRequest): MemoDto
    fun update(memoId: Uuid, request: UpdateMemoRequest): MemoDto
    fun delete(memoId: Uuid)
    fun search(groupId: Uuid, query: String): List<MemoDto>
    fun countPinned(groupId: Uuid): Int
}
