package org.bmsk.lifemash.feature.shared.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
internal fun AllRoute() {
    AllScreen()
}

@Composable
private fun AllScreen() {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        TopBar()
        Column(
            Modifier
                .padding(horizontal = 8.dp)
                .verticalScroll(scrollState)
                .padding(bottom = 4.dp),
        ) {
            Title()
            BookmarkMenu()
        }
    }
}

@Composable
private fun TopBar() {
    Row(modifier = Modifier.fillMaxWidth()) {}
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
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
        )
    }
}
