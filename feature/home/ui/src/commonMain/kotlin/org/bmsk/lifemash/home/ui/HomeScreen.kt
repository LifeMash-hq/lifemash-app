package org.bmsk.lifemash.home.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.bmsk.lifemash.home.api.BlocksTodayData
import org.bmsk.lifemash.home.api.HomeBlock
import org.bmsk.lifemash.home.ui.blocks.AssistantBlock
import org.bmsk.lifemash.home.ui.blocks.CalendarTodayBlock
import org.bmsk.lifemash.home.ui.blocks.GroupsBlock
import org.bmsk.lifemash.home.ui.blocks.WebViewBlock
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HomeRouteScreen(
    onNavigateToBlockSettings: () -> Unit,
    onNavigateToAssistant: () -> Unit,
    viewModel: HomeViewModel = koinViewModel(),
) {
    val blocks by viewModel.blocks.collectAsState()
    val todayData by viewModel.todayData.collectAsState()
    val accessToken by viewModel.accessToken.collectAsState()

    HomeScreen(
        blocks = blocks,
        todayData = todayData,
        accessToken = accessToken,
        onNavigateToBlockSettings = onNavigateToBlockSettings,
        onNavigateToAssistant = onNavigateToAssistant,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HomeScreen(
    blocks: List<HomeBlock>,
    todayData: BlocksTodayData?,
    accessToken: String?,
    onNavigateToBlockSettings: () -> Unit,
    onNavigateToAssistant: () -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(title = { Text("홈") })
            if (blocks.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    val visibleBlocks = blocks.filter { it.visible }
                    itemsIndexed(visibleBlocks, key = { _, block -> block.id }) { _, block ->
                        when (block) {
                            is HomeBlock.CalendarToday -> CalendarTodayBlock(todayData)
                            is HomeBlock.Groups -> GroupsBlock(todayData)
                            is HomeBlock.Assistant -> AssistantBlock(onNavigateToAssistant)
                            is HomeBlock.WebViewBlock -> WebViewBlock(block.url) { accessToken }
                        }
                    }
                }
            }
        }
        FloatingActionButton(
            onClick = onNavigateToBlockSettings,
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
        ) {
            Icon(imageVector = Icons.Filled.Settings, contentDescription = "블록 설정")
        }
    }
}
