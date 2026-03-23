package org.bmsk.lifemash.home.ui.blocks

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.bmsk.lifemash.home.api.BlocksTodayData

@Composable
internal fun GroupsBlock(todayData: BlocksTodayData?) {
    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "그룹", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            if (todayData == null || todayData.groups.isEmpty()) {
                Text(
                    text = "그룹이 없습니다",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                todayData.groups.forEach { group ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Group,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                        )
                        Spacer(Modifier.width(8.dp))
                        Column {
                            Text(text = group.name, style = MaterialTheme.typography.bodyMedium)
                            Text(
                                text = "${group.memberCount}명",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }
        }
    }
}
