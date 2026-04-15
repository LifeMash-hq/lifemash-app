@file:OptIn(kotlin.time.ExperimentalTime::class)

package org.bmsk.lifemash.notification.impl

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import lifemash.feature.notification.impl.generated.resources.Res
import lifemash.feature.notification.impl.generated.resources.notification_action_comment
import lifemash.feature.notification.impl.generated.resources.notification_action_follow
import lifemash.feature.notification.impl.generated.resources.notification_action_like
import lifemash.feature.notification.impl.generated.resources.notification_action_photo
import lifemash.feature.notification.impl.generated.resources.notification_event_reminder
import lifemash.feature.notification.impl.generated.resources.notification_generic_default
import org.jetbrains.compose.resources.StringResource
import kotlin.time.Instant

/**
 * Screen이 직접 렌더링하는 표현 모델.
 *
 * 도메인 [org.bmsk.lifemash.domain.notification.Notification]의 nullable 필드들이 종류별로
 * 어떤 조합이 valid한지 컴파일러가 알 수 없으므로, 종류별로 sealed 분기를 두어 invariant를
 * 타입으로 강제한다. ViewModel이 변환을 책임지고 Screen은 sealed 분기 없이
 * `actorName/quote/actionText` 공통 속성만 읽어 렌더링한다.
 */
@Immutable
internal sealed interface NotificationUi {
    val id: String
    val isUnread: Boolean
    val createdAt: Instant
    val visual: NotificationVisual
    val targetId: String?

    // Screen 본문 렌더링에 필요한 공통 표현 속성. 변형별로 자기 시멘틱 필드를 매핑해 노출한다.
    val actorName: String?
    val quote: String?
    val actionText: ActionText

    /**
     * 알림 본문의 동작 문구. 다국어 자원과 런타임 문자열을 모두 수용한다.
     */
    sealed interface ActionText {
        data class Resource(
            val res: StringResource,
            val formatArgs: PersistentList<Any> = persistentListOf(),
        ) : ActionText

        data class Literal(val text: String) : ActionText
    }

    data class Comment(
        override val id: String,
        override val isUnread: Boolean,
        override val createdAt: Instant,
        override val targetId: String?,
        override val actorName: String,
        override val quote: String,
    ) : NotificationUi {
        override val visual = NotificationVisual.COMMENT
        override val actionText = ActionText.Resource(Res.string.notification_action_comment)
    }

    data class Follow(
        override val id: String,
        override val isUnread: Boolean,
        override val createdAt: Instant,
        override val targetId: String?,
        override val actorName: String,
    ) : NotificationUi {
        override val visual = NotificationVisual.FOLLOW
        override val quote: String? = null
        override val actionText = ActionText.Resource(Res.string.notification_action_follow)
    }

    data class Like(
        override val id: String,
        override val isUnread: Boolean,
        override val createdAt: Instant,
        override val targetId: String?,
        override val actorName: String,
    ) : NotificationUi {
        override val visual = NotificationVisual.LIKE
        override val quote: String? = null
        override val actionText = ActionText.Resource(Res.string.notification_action_like)
    }

    data class Photo(
        override val id: String,
        override val isUnread: Boolean,
        override val createdAt: Instant,
        override val targetId: String?,
        override val actorName: String,
        val caption: String,
    ) : NotificationUi {
        override val visual = NotificationVisual.PHOTO
        override val quote: String get() = caption
        override val actionText = ActionText.Resource(Res.string.notification_action_photo)
    }

    data class EventReminder(
        override val id: String,
        override val isUnread: Boolean,
        override val createdAt: Instant,
        override val targetId: String?,
        val eventTitle: String,
    ) : NotificationUi {
        override val visual = NotificationVisual.EVENT
        override val actorName: String? = null
        override val quote: String? = null
        override val actionText = ActionText.Resource(
            res = Res.string.notification_event_reminder,
            formatArgs = persistentListOf(eventTitle),
        )
    }

    data class Generic(
        override val id: String,
        override val isUnread: Boolean,
        override val createdAt: Instant,
        override val targetId: String?,
        override val actorName: String?,
        val text: String?,
    ) : NotificationUi {
        override val visual = NotificationVisual.GENERIC
        override val quote: String? = null
        override val actionText: ActionText = text?.let { ActionText.Literal(it) }
            ?: ActionText.Resource(Res.string.notification_generic_default)
    }
}
