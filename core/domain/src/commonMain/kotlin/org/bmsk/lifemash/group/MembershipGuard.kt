package org.bmsk.lifemash.group

interface MembershipGuard {
    /** groupId 그룹의 멤버가 아니면 ForbiddenException 발생 */
    fun require(groupId: String, userId: String)
}
