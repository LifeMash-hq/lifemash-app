package org.bmsk.lifemash.memo

import org.bmsk.lifemash.group.MembershipGuard
import org.bmsk.lifemash.model.memo.ChecklistItemDto
import org.bmsk.lifemash.model.memo.CreateMemoRequest
import org.bmsk.lifemash.model.memo.MemoDto
import org.bmsk.lifemash.model.memo.SyncChecklistRequest
import org.bmsk.lifemash.model.memo.UpdateMemoRequest
import org.bmsk.lifemash.notification.FcmService
import org.bmsk.lifemash.plugins.BadRequestException
import org.bmsk.lifemash.plugins.NotFoundException
import kotlin.uuid.Uuid

private const val MAX_PINNED = 5

class MemoServiceImpl(
    private val memoRepository: MemoRepository,
    private val checklistItemRepository: ChecklistItemRepository,
    private val membershipGuard: MembershipGuard,
    private val fcmService: FcmService,
) : MemoService {

    override fun getGroupMemos(groupId: String, userId: String): List<MemoDto> {
        membershipGuard.require(groupId, userId)
        return memoRepository.findByGroup(Uuid.parse(groupId))
    }

    override fun getMemo(groupId: String, memoId: String, userId: String): MemoDto {
        membershipGuard.require(groupId, userId)
        return memoRepository.findById(Uuid.parse(memoId))
            ?: throw NotFoundException("Memo not found")
    }

    override fun create(groupId: String, userId: String, request: CreateMemoRequest): MemoDto {
        membershipGuard.require(groupId, userId)
        if (request.isPinned) requirePinSlot(groupId)
        val memo = memoRepository.create(Uuid.parse(groupId), Uuid.parse(userId), request)
        fcmService.notifyGroupExcept(Uuid.parse(groupId), Uuid.parse(userId), "새 메모", memo.title)
        return memo
    }

    override fun update(groupId: String, memoId: String, userId: String, request: UpdateMemoRequest): MemoDto {
        membershipGuard.require(groupId, userId)
        val existing = memoRepository.findById(Uuid.parse(memoId))
            ?: throw NotFoundException("Memo not found")
        if (request.isPinned == true && !existing.isPinned) requirePinSlot(groupId)
        val updated = memoRepository.update(Uuid.parse(memoId), request)
        fcmService.notifyGroupExcept(Uuid.parse(groupId), Uuid.parse(userId), "메모 수정", updated.title)
        return updated
    }

    override fun delete(groupId: String, memoId: String, userId: String) {
        membershipGuard.require(groupId, userId)
        memoRepository.findById(Uuid.parse(memoId)) ?: throw NotFoundException("Memo not found")
        memoRepository.delete(Uuid.parse(memoId))
    }

    override fun search(groupId: String, userId: String, query: String): List<MemoDto> {
        membershipGuard.require(groupId, userId)
        return memoRepository.search(Uuid.parse(groupId), query)
    }

    override fun syncChecklist(
        groupId: String,
        memoId: String,
        userId: String,
        request: SyncChecklistRequest,
    ): List<ChecklistItemDto> {
        membershipGuard.require(groupId, userId)
        memoRepository.findById(Uuid.parse(memoId)) ?: throw NotFoundException("Memo not found")
        return checklistItemRepository.sync(Uuid.parse(memoId), request.items)
    }

    private fun requirePinSlot(groupId: String) {
        if (memoRepository.countPinned(Uuid.parse(groupId)) >= MAX_PINNED) {
            throw BadRequestException("핀 메모는 그룹당 최대 ${MAX_PINNED}개까지 가능합니다")
        }
    }
}
