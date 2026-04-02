package org.bmsk.lifemash.memo

import org.bmsk.lifemash.model.memo.ChecklistItemDto
import org.bmsk.lifemash.model.memo.CreateMemoRequest
import org.bmsk.lifemash.model.memo.MemoDto
import org.bmsk.lifemash.model.memo.SyncChecklistRequest
import org.bmsk.lifemash.model.memo.UpdateMemoRequest

interface MemoService {
    fun getGroupMemos(groupId: String, userId: String): List<MemoDto>
    fun getMemo(groupId: String, memoId: String, userId: String): MemoDto
    fun create(groupId: String, userId: String, request: CreateMemoRequest): MemoDto
    fun update(groupId: String, memoId: String, userId: String, request: UpdateMemoRequest): MemoDto
    fun delete(groupId: String, memoId: String, userId: String)
    fun search(groupId: String, userId: String, query: String): List<MemoDto>
    fun syncChecklist(groupId: String, memoId: String, userId: String, request: SyncChecklistRequest): List<ChecklistItemDto>
}
