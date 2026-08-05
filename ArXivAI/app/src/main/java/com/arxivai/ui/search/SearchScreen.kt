package com.arxivai.ui.search

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.arxivai.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onPaperClick: (String) -> Unit,
    viewModel: SearchViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        // Search bar
        TopAppBar(
            title = {
                TextField(
                    value = uiState.query,
                    onValueChange = { viewModel.onQueryChanged(it) },
                    placeholder = { Text("Search papers, authors, topics...") },
                    leadingIcon = {
                        Icon(Icons.Filled.Search, contentDescription = null)
                    },
                    trailingIcon = {
                        if (uiState.query.isNotEmpty()) {
                            IconButton(onClick = { viewModel.onQueryChanged("") }) {
                                Icon(Icons.Filled.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                        unfocusedIndicatorColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    shape = MaterialTheme.shapes.large,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background
            )
        )

        // Content
        when {
            uiState.isLoading -> {
                LoadingIndicator()
            }
            uiState.error != null && uiState.localResults.isEmpty() -> {
                ErrorState(
                    message = uiState.error ?: "Search failed",
                    onRetry = {
                        if (uiState.query.isNotEmpty()) {
                            viewModel.onQueryChanged(uiState.query)
                        }
                    }
                )
            }
            !uiState.hasSearched && uiState.query.isEmpty() -> {
                EmptyState(
                    icon = Icons.Filled.Search,
                    title = "Search ArXiv",
                    description = "Search millions of academic papers across all categories"
                )
            }
            uiState.hasSearched && uiState.localResults.isEmpty() && uiState.results.isEmpty() -> {
                EmptyState(
                    icon = Icons.Filled.SearchOff,
                    title = "No results found",
                    description = "Try different keywords or browse categories"
                )
            }
            else -> {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Local results header
                    if (uiState.localResults.isNotEmpty()) {
                        item {
                            Text(
                                text = "Local Results",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                        items(
                            items = uiState.localResults,
                            key = { it.id }
                        ) { paper ->
                            PaperCard(
                                paper = paper,
                                onPaperClick = { onPaperClick(paper.id) },
                                onBookmarkClick = { viewModel.toggleBookmark(paper) }
                            )
                        }
                    }

                    // API results header
                    if (uiState.results.isNotEmpty()) {
                        item {
                            Text(
                                text = "ArXiv Results",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                        items(
                            items = uiState.results,
                            key = { it.id }
                        ) { paper ->
                            PaperCard(
                                paper = paper,
                                onPaperClick = { onPaperClick(paper.id) },
                                onBookmarkClick = { viewModel.toggleBookmark(paper) }
                            )
                        }
                    }
                }
            }
        }
    }
}