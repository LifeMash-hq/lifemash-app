package org.bmsk.lifemash.group

import org.bmsk.lifemash.model.calendar.CreateGroupRequest
import org.bmsk.lifemash.model.calendar.GroupDto
import org.bmsk.lifemash.model.calendar.JoinGroupRequest
import org.bmsk.lifemash.model.calendar.UpdateGroupRequest
import org.bmsk.lifemash.plugins.ForbiddenException
import org.bmsk.lifemash.plugins.NotFoundException
import org.bmsk.lifemash.validation.GroupName
import kotlin.uuid.Uuid

/**
 * 그룹 비즈니스 로직 서비스.
 *
 * 백엔드의 3계층 아키텍처:
 *   Routes(라우트) → Service(서비스) → Repository(저장소)
 *
 * - Routes: HTTP 요청 수신/응답. String → UUID 파싱 담당
 * - Service: 비즈니스 규칙 적용 (권한 검사, 도메인 모델 위임 등). UUID 타입으로 받음
 * - Repository: DB 쿼리 수행
 */
class GroupServiceImpl(private val repository: GroupRepository) : GroupService {

    override fun create(userId: Uuid, request: CreateGroupRequest): GroupDto =
        repository.create(userId, request.type, request.name)

    override fun join(userId: Uuid, request: JoinGroupRequest): GroupDto =
        repository.join(userId, request.inviteCode)

    override fun getMyGroups(userId: Uuid): List<GroupDto> =
        repository.findByUserId(userId)

    override fun getGroup(groupId: Uuid): GroupDto =
        repository.findById(groupId) ?: throw NotFoundException("Group not found")

    override fun delete(groupId: Uuid, userId: Uuid) =
        repository.delete(groupId, userId)

    override fun updateName(groupId: Uuid, userId: Uuid, request: UpdateGroupRequest): GroupDto {
        val name = GroupName.of(request.name)   // 도메인 모델이 검증 담당

        val group = repository.findById(groupId)
            ?: throw NotFoundException("그룹을 찾을 수 없습니다")
        val member = group.members.find { it.userId == userId.toString() }
            ?: throw ForbiddenException("그룹 멤버가 아닙니다")
        if (member.role != "OWNER") throw ForbiddenException("그룹장만 이름을 변경할 수 있습니다")

        return repository.updateName(groupId, name.value)
    }
}
