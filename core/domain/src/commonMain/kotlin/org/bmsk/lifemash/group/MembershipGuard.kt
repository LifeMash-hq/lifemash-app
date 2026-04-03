package org.bmsk.lifemash.group

interface MembershipGuard {
    /** groupId 그룹의 멤버가 아니면 ForbiddenException 발생 */
    fun require(groupId: String, userId: String)

    /**
     * eventId로 그룹을 해소한 뒤 멤버십 검증.
     * 검증된 groupId를 반환 (FCM 등 후속 처리용).
     */
    fun requireByEvent(eventId: String, userId: String): String
}
