package org.bmsk.lifemash.feature.all

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.bmsk.lifemash.core.designsystem.theme.LifeMashTheme

@Composable
internal fun AllRoute() {
    AllScreen()
}

@Composable
private fun AllScreen() {
    val scrollState = rememberScrollState()
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { TopBar() },
        content = { contentPadding ->
            Column(
                Modifier
                    .padding(contentPadding)
                    .padding(horizontal = 8.dp)
                    .verticalScroll(scrollState)
                    .padding(bottom = 4.dp),
            ) {
                Title()
                BookmarkMenu()
            }
        },
    )
}

@Composable
private fun TopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
    ) {
    }
}

@Composable
private fun Title() {
    Text(
        text = "전체",
        style = MaterialTheme.typography.headlineLarge,
        modifier = Modifier.padding(bottom = 12.dp),
    )
}

@Composable
private fun BookmarkMenu() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(text = "북마크 뉴스", style = MaterialTheme.typography.titleMedium)
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.baseline_keyboard_arrow_right_24),
            contentDescription = null,
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun AllScreenPreview() {
    LifeMashTheme {
        AllScreen()
    }
}
