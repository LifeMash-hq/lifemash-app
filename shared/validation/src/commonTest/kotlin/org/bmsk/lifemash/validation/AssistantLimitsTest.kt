package org.bmsk.lifemash.validation

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AssistantLimitsTest {

    @Test
    fun `can send when under limit without api key`() {
        assertTrue(AssistantLimits.canSendMessage(requestCount = 0, hasUserApiKey = false))
        assertTrue(AssistantLimits.canSendMessage(requestCount = 19, hasUserApiKey = false))
    }

    @Test
    fun `cannot send when at limit without api key`() {
        assertFalse(AssistantLimits.canSendMessage(requestCount = 20, hasUserApiKey = false))
        assertFalse(AssistantLimits.canSendMessage(requestCount = 100, hasUserApiKey = false))
    }

    @Test
    fun `can always send with api key`() {
        assertTrue(AssistantLimits.canSendMessage(requestCount = 0, hasUserApiKey = true))
        assertTrue(AssistantLimits.canSendMessage(requestCount = 20, hasUserApiKey = true))
        assertTrue(AssistantLimits.canSendMessage(requestCount = 100, hasUserApiKey = true))
    }
}
