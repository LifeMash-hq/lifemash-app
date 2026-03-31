package org.bmsk.lifemash.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import org.bmsk.lifemash.designsystem.theme.LifeMashRadius
import org.bmsk.lifemash.designsystem.theme.LifeMashSpacing
import org.bmsk.lifemash.designsystem.theme.LocalLifeMashColors

@Composable
fun LifeMashNotificationItem(
    emoji: String,
    emojiBackground: Color,
    bodyText: @Composable () -> Unit,
    timeText: String,
    isUnread: Boolean,
    modifier: Modifier = Modifier,
) {
    val unreadBg = LocalLifeMashColors.current.unreadBg
    val primaryColor = MaterialTheme.colorScheme.primary
    val borderWidth = 3.dp

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (isUnread) {
                        Modifier
                            .background(unreadBg)
                            .drawBehind {
                                drawRect(
                                    color = primaryColor,
                                    topLeft = Offset.Zero,
                                    size = size.copy(width = borderWidth.toPx()),
                                )
                            }
                    } else {
                        Modifier
                    },
                )
                .padding(
                    horizontal = LifeMashSpacing.xl,
                    vertical = LifeMashSpacing.lg - LifeMashSpacing.micro,
                ),
            verticalAlignment = Alignment.Top,
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(emojiBackground, RoundedCornerShape(LifeMashRadius.md)),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = emoji, style = MaterialTheme.typography.titleMedium)
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = LifeMashSpacing.md),
            ) {
                bodyText()
                Text(
                    text = timeText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = LifeMashSpacing.micro),
                )
            }
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
    }
}

@Composable
fun NotificationBodyText(
    actorName: String?,
    actionText: String,
    quote: String? = null,
) {
    val text = buildAnnotatedString {
        if (actorName != null) {
            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                append(actorName)
            }
            append(" ")
        }
        append(actionText)
        if (quote != null) {
            append(" \u2014 ")
            append("\"$quote\"")
        }
    }
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurface,
    )
}
