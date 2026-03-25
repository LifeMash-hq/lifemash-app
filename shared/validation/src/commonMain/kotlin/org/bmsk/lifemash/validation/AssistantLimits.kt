package org.bmsk.lifemash.validation

object AssistantLimits {
    const val MAX_MESSAGE_LENGTH = ChatMessageContent.MAX_LENGTH
    const val DAILY_REQUEST_LIMIT = 20

    fun canSendMessage(requestCount: Int, hasUserApiKey: Boolean): Boolean {
        if (hasUserApiKey) return true
        return requestCount < DAILY_REQUEST_LIMIT
    }
}
