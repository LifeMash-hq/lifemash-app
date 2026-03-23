package org.bmsk.lifemash.home.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.bmsk.lifemash.home.api.HomeBlock
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun BlockSettingsRouteScreen(
    onBack: () -> Unit,
    onNavigateToMarketplace: () -> Unit,
    viewModel: HomeViewModel = koinViewModel(),
) {
    val blocks by viewModel.blocks.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("블록 설정") },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로")
                }
            },
        )
        LazyColumn {
            itemsIndexed(blocks, key = { _, block -> block.id }) { index, block ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(text = block.label(), modifier = Modifier.weight(1f))
                    if (block is HomeBlock.WebViewBlock) {
                        IconButton(onClick = { viewModel.removeBlock(block) }) {
                            Icon(Icons.Filled.Close, contentDescription = "제거")
                        }
                    }
                    Switch(
                        checked = block.visible,
                        onCheckedChange = { viewModel.toggleVisibility(block) },
                    )
                    IconButton(
                        onClick = { viewModel.moveBlockUp(index) },
                        enabled = index > 0,
                    ) {
                        Icon(Icons.Filled.KeyboardArrowUp, contentDescription = "위로")
                    }
                    IconButton(
                        onClick = { viewModel.moveBlockDown(index) },
                        enabled = index < blocks.size - 1,
                    ) {
                        Icon(Icons.Filled.KeyboardArrowDown, contentDescription = "아래로")
                    }
                }
            }
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onNavigateToMarketplace,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                ) {
                    Text("마켓플레이스 둘러보기")
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

private fun HomeBlock.label(): String = when (this) {
    is HomeBlock.CalendarToday -> "오늘 일정"
    is HomeBlock.Groups -> "그룹"
    is HomeBlock.Assistant -> "AI 어시스턴트"
    is HomeBlock.WebViewBlock -> blockId.take(8).let { "앱 블록 ($it)" }
}
