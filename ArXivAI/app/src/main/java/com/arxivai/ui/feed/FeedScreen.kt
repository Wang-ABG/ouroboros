package com.arxivai.ui.feed

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.arxivai.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    onPaperClick: (String) -> Unit,
    viewModel: FeedViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Top bar
        TopAppBar(
            title = {
                Column {
                    Text(
                        text = "ArXivAI",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    if (uiState.isLoading) {
                        LinearProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            },
            actions = {
                IconButton(onClick = { viewModel.refresh() }) {
                    Icon(Icons.Filled.Refresh, contentDescription = "Refresh")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background
            )
        )

        // Category selector
        CategorySelector(
            selectedCategory = uiState.selectedCategory,
            onCategorySelected = { viewModel.selectCategory(it) },
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        // Content
        when {
            uiState.isLoading && !uiState.isRefreshing && uiState.papers.isEmpty() -> {
                LoadingIndicator()
            }
            uiState.error != null && uiState.papers.isEmpty() -> {
                ErrorState(
                    message = uiState.error ?: "Something went wrong",
                    onRetry = { viewModel.fetchPapers() }
                )
            }
            uiState.papers.isEmpty() -> {
                EmptyState(
                    icon = Icons.Filled.Article,
                    title = "No papers found",
                    description = "Try selecting a different category",
                    modifier = Modifier.weight(1f)
                )
            }
            else -> {
                Box(modifier = Modifier.weight(1f)) {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            items = uiState.papers,
                            key = { it.id }
                        ) { paper ->
                            PaperCard(
                                paper = paper,
                                onPaperClick = { onPaperClick(paper.id) },
                                onBookmarkClick = { viewModel.toggleBookmark(paper) }
                            )
                        }
                    }

                    // Pull-to-refresh indicator at top
                    if (uiState.isRefreshing) {
                        LinearProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.TopCenter),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}