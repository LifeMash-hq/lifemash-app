package org.bmsk.lifemash.home.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MarketplaceRouteScreen(
    onBack: () -> Unit,
    viewModel: MarketplaceViewModel = koinViewModel(),
) {
    val blocks by viewModel.blocks.collectAsState()
    val installedIds by viewModel.installedIds.collectAsState()
    val installing by viewModel.installing.collectAsState()
    val error by viewModel.error.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("마켓플레이스") },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로")
                }
            },
        )
        val errorMessage = error
        if (errorMessage != null) {
            Box(
                modifier = Modifier.fillMaxSize().padding(24.dp),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "오류: $errorMessage",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { viewModel.retry() }) { Text("다시 시도") }
                }
            }
        } else {
            LazyColumn {
                items(blocks, key = { it.id }) { block ->
                    MarketplaceBlockItem(
                        block = block,
                        isInstalled = block.id in installedIds,
                        isInstalling = installing == block.id,
                        onInstall = { viewModel.installBlock(block) },
                    )
                }
            }
        }
    }
}

@Composable
private fun MarketplaceBlockItem(
    block: org.bmsk.lifemash.home.api.MarketplaceBlockInfo,
    isInstalled: Boolean,
    isInstalling: Boolean,
    onInstall: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = block.name, style = MaterialTheme.typography.titleMedium)
            val description = block.description
            if (!description.isNullOrBlank()) {
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        when {
            isInstalling -> CircularProgressIndicator()
            isInstalled -> Button(onClick = {}, enabled = false) { Text("설치됨") }
            else -> Button(onClick = onInstall) { Text("추가") }
        }
    }
}
