package com.arxivai.ui.bookmarks

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
import com.arxivai.ui.components.EmptyState
import com.arxivai.ui.components.LoadingIndicator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookmarksScreen(
    onPaperClick: (String) -> Unit,
    viewModel: BookmarksViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                Text(
                    text = "Bookmarks",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background
            )
        )

        when {
            uiState.isLoading -> LoadingIndicator()
            uiState.bookmarks.isEmpty() -> {
                EmptyState(
                    icon = Icons.Filled.BookmarkBorder,
                    title = "Your bookmarks are empty",
                    description = "Papers you bookmark will appear here",
                    modifier = Modifier.weight(1f)
                )
            }
            else -> {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Header with count
                    item {
                        Text(
                            text = "${uiState.bookmarks.size} bookmarked papers",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    items(
                        items = uiState.bookmarks,
                        key = { it.paperId }
                    ) { bookmark ->
                        BookmarkCard(
                            bookmark = bookmark,
                            onClick = { onPaperClick(bookmark.paperId) },
                            onRemove = { viewModel.removeBookmark(bookmark.paperId) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BookmarkCard(
    bookmark: com.arxivai.data.local.entity.BookmarkEntity,
    onClick: () -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = bookmark.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                Row {
                    Text(
                        text = "arXiv:${bookmark.paperId}",
                        style = MaterialTheme.typography.labelSmall,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = bookmark.categories.take(30),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                }
            }
            IconButton(onClick = onRemove) {
                Icon(
                    Icons.Filled.Bookmark,
                    contentDescription = "Remove bookmark",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}