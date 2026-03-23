package org.bmsk.lifemash.home.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.bmsk.lifemash.home.api.BlocksTodayData
import org.bmsk.lifemash.home.api.HomeBlock
import org.bmsk.lifemash.home.ui.blocks.AssistantBlock
import org.bmsk.lifemash.home.ui.blocks.BridgeWebView
import org.bmsk.lifemash.home.ui.blocks.CalendarTodayBlock
import org.bmsk.lifemash.home.ui.blocks.GroupsBlock
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

    LaunchedEffect(Unit) {
        viewModel.loadInitialData()
    }

    HomeScreen(
        blocks = blocks,
        todayData = todayData,
        accessToken = accessToken,
        onNavigateToBlockSettings = onNavigateToBlockSettings,
        onNavigateToAssistant = onNavigateToAssistant,
    )
}

@Composable
internal fun HomeScreen(
    blocks: List<HomeBlock>,
    todayData: BlocksTodayData?,
    accessToken: String?,
    onNavigateToBlockSettings: () -> Unit,
    onNavigateToAssistant: () -> Unit,
) {
    if (blocks.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val visibleBlocks = blocks.filter { it.visible }
    val webBlocks = visibleBlocks.filterIsInstance<HomeBlock.WebViewBlock>()
    val pageCount = 1 + webBlocks.size
    val pagerState = rememberPagerState { pageCount }
    val coroutineScope = rememberCoroutineScope()

    val pageNames = buildList {
        add("대시보드")
        webBlocks.forEach { add(it.blockId) }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            if (pageCount > 1) {
                ScrollableTabRow(
                    selectedTabIndex = pagerState.currentPage,
                    edgePadding = 16.dp,
                ) {
                    pageNames.forEachIndexed { index, name ->
                        Tab(
                            selected = pagerState.currentPage == index,
                            onClick = {
                                coroutineScope.launch { pagerState.animateScrollToPage(index) }
                            },
                            text = {
                                Text(
                                    text = name,
                                    style = MaterialTheme.typography.labelLarge,
                                )
                            },
                        )
                    }
                }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                key = { if (it == 0) "dashboard" else webBlocks[it - 1].id },
            ) { page ->
                if (page == 0) {
                    DashboardPage(
                        todayData = todayData,
                        onNavigateToAssistant = onNavigateToAssistant,
                    )
                } else {
                    val block = webBlocks[page - 1]
                    BridgeWebView(
                        url = block.url,
                        tokenProvider = { accessToken },
                        modifier = Modifier.fillMaxSize(),
                    )
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

@Composable
private fun DashboardPage(
    todayData: BlocksTodayData?,
    onNavigateToAssistant: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        item { CalendarTodayBlock(todayData) }
        item { Spacer(Modifier.height(12.dp)) }
        item { GroupsBlock(todayData) }
        item { Spacer(Modifier.height(12.dp)) }
        item { AssistantBlock(onNavigateToAssistant) }
    }
}
