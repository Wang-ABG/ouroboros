package com.arxivai.ui.detail

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.arxivai.ui.components.ErrorState
import com.arxivai.ui.components.LoadingIndicator
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    paperId: String,
    onNavigateBack: () -> Unit,
    viewModel: DetailViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val scrollState = rememberScrollState()

    LaunchedEffect(paperId) {
        viewModel.loadPaper(paperId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Share
                    IconButton(onClick = {
                        uiState.paper?.let { paper ->
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, paper.title)
                                putExtra(Intent.EXTRA_TEXT,
                                    "${paper.title}\n\narXiv:${paper.id}\nhttps://arxiv.org/abs/${paper.id}"
                                )
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Share paper"))
                        }
                    }) {
                        Icon(Icons.Outlined.Share, contentDescription = "Share")
                    }
                    // Open in browser
                    IconButton(onClick = {
                        uiState.paper?.let { paper ->
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://arxiv.org/abs/${paper.id}"))
                            context.startActivity(intent)
                        }
                    }) {
                        Icon(Icons.Outlined.OpenInBrowser, contentDescription = "Open in browser")
                    }
                    // Bookmark
                    IconButton(onClick = { viewModel.toggleBookmark() }) {
                        Icon(
                            imageVector = if (uiState.isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                            contentDescription = "Bookmark",
                            tint = if (uiState.isBookmarked) MaterialTheme.colorScheme.primary
                                   else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        when {
            uiState.isLoading -> {
                LoadingIndicator(modifier = Modifier.padding(padding))
            }
            uiState.error != null && uiState.paper == null -> {
                ErrorState(
                    message = uiState.error ?: "Failed to load paper",
                    onRetry = { viewModel.loadPaper(paperId) },
                    modifier = Modifier.padding(padding)
                )
            }
            uiState.paper != null -> {
                val paper = uiState.paper!!
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 20.dp)
                        .verticalScroll(scrollState)
                ) {
                    // Categories
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(paper.categories.split(",").map { it.trim() }) { category ->
                            SuggestionChip(
                                onClick = { },
                                label = {
                                    Text(
                                        text = category,
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                },
                                shape = RoundedCornerShape(6.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Title
                    Text(
                        text = paper.title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Authors
                    Text(
                        text = paper.authors,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // arXiv ID + Date
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "arXiv:${paper.id}",
                            style = MaterialTheme.typography.labelMedium,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "•",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = formatDate(paper.publishedDate),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Action buttons
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilledTonalButton(
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(paper.pdfUrl))
                                context.startActivity(intent)
                            },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Filled.PictureAsPdf, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("PDF")
                        }
                        OutlinedButton(
                            onClick = {
                                clipboardManager.setText(AnnotatedString(paper.id))
                            },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Filled.ContentCopy, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Copy ID")
                        }
                    }

                    // AI Summary Section
                    Spacer(modifier = Modifier.height(24.dp))
                    if (uiState.isSummarizing) {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f)
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp
                                )
                                Spacer(Modifier.width(12.dp))
                                Text(
                                    text = "Generating AI summary...",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    } else if (uiState.aiSummary.isNotEmpty()) {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f)
                            )
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Filled.Psychology,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.tertiary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        text = "AI Summary",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.tertiary
                                    )
                                }
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = uiState.aiSummary,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer
                                )
                            }
                        }
                    }

                    // Keywords
                    if (uiState.keywords.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Key Phrases",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(Modifier.height(8.dp))
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            items(uiState.keywords) { keyword ->
                                AssistChip(
                                    onClick = { },
                                    label = { Text(keyword, style = MaterialTheme.typography.labelMedium) },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = AssistChipDefaults.assistChipColors(
                                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                                    )
                                )
                            }
                        }
                    }

                    // Abstract
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "Abstract",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = paper.abstract,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

private fun formatDate(dateStr: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
        inputFormat.timeZone = TimeZone.getTimeZone("UTC")
        val date = inputFormat.parse(dateStr) ?: return dateStr
        val outputFormat = SimpleDateFormat("MMMM d, yyyy", Locale.US)
        outputFormat.format(date)
    } catch (e: Exception) {
        dateStr.take(10)
    }
}