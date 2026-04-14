package org.bmsk.lifemash.notification.impl

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import kotlinx.collections.immutable.persistentListOf
import kotlin.time.Clock
import org.bmsk.lifemash.domain.notification.Notification
import org.bmsk.lifemash.domain.notification.NotificationType
import org.junit.Rule
import org.junit.Test

class NotificationScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun empty_상태에서_안내_텍스트가_표시된다() {
        composeTestRule.setContent {
            NotificationScreen(
                uiState = NotificationUiState.Empty,
                onRetry = {},
            )
        }

        composeTestRule.onNodeWithText("새로운 알림이 없어요").assertIsDisplayed()
    }

    @Test
    fun loaded_상태에서_알림_목록이_표시된다() {
        val notifications = persistentListOf(
            Notification(
                id = "1",
                type = NotificationType.COMMENT,
                actorNickname = "이수아",
                actorProfileImage = null,
                targetId = null,
                content = "축하해!",
                isRead = false,
                createdAt = Clock.System.now(),
            ),
            Notification(
                id = "2",
                type = NotificationType.FOLLOW,
                actorNickname = "정재원",
                actorProfileImage = null,
                targetId = null,
                content = null,
                isRead = true,
                createdAt = Clock.System.now(),
            ),
        )
        composeTestRule.setContent {
            NotificationScreen(
                uiState = NotificationUiState.Loaded(notifications),
                onRetry = {},
            )
        }

        composeTestRule.onNodeWithText("이수아", substring = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("정재원", substring = true).assertIsDisplayed()
    }

    @Test
    fun error_상태에서_다시_시도_버튼이_표시된다() {
        composeTestRule.setContent {
            NotificationScreen(
                uiState = NotificationUiState.Error("오류 발생"),
                onRetry = {},
            )
        }

        composeTestRule.onNodeWithText("다시 시도").assertIsDisplayed()
    }
}
