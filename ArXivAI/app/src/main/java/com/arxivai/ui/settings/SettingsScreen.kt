package com.arxivai.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Divider
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.arxivai.ui.components.arXivCategories

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showCategoryDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                Text(
                    text = "Settings",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background
            )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            // Appearance Section
            Text(
                text = "Appearance",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
            )

            // Theme selection
            SettingsCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.DarkMode, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.width(12.dp))
                    Text("Theme", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
                    var expanded by remember { mutableStateOf(false) }
                    Box {
                        TextButton(onClick = { expanded = true }) {
                            Text(
                                text = when (uiState.themeMode) {
                                    "light" -> "Light"
                                    "dark" -> "Dark"
                                    else -> "System"
                                }
                            )
                            Icon(Icons.Filled.ArrowDropDown, contentDescription = null)
                        }
                        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            DropdownMenuItem(
                                text = { Text("System default") },
                                onClick = { viewModel.setThemeMode("system"); expanded = false }
                            )
                            DropdownMenuItem(
                                text = { Text("Light") },
                                onClick = { viewModel.setThemeMode("light"); expanded = false }
                            )
                            DropdownMenuItem(
                                text = { Text("Dark") },
                                onClick = { viewModel.setThemeMode("dark"); expanded = false }
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Content Section
            Text(
                text = "Content",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            SettingsCard {
                // Default category
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.Category, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Default Category", style = MaterialTheme.typography.bodyLarge)
                        Text(
                            text = uiState.defaultCategory.ifEmpty { "All" },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    TextButton(onClick = { showCategoryDialog = true }) {
                        Text("Change")
                    }
                }

                Divider(modifier = Modifier.padding(vertical = 4.dp))

                // Auto-summarize
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.Psychology, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("AI Summary", style = MaterialTheme.typography.bodyLarge)
                        Text(
                            text = "On-device text summarization",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = uiState.aiSummaryEnabled,
                        onCheckedChange = { viewModel.setAiSummaryEnabled(it) }
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // About Section
            Text(
                text = "About",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            SettingsCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.Info, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("ArXivAI", style = MaterialTheme.typography.bodyLarge)
                        Text("Version 1.0.0", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Divider(modifier = Modifier.padding(vertical = 4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.Article, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Powered by arXiv API", style = MaterialTheme.typography.bodyLarge)
                        Text("On-device AI via TextRank algorithm", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }

    // Category selection dialog
    if (showCategoryDialog) {
        AlertDialog(
            onDismissRequest = { showCategoryDialog = false },
            title = { Text("Default Category") },
            text = {
                Column {
                    arXivCategories.forEach { category ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = uiState.defaultCategory == category.code,
                                onClick = {
                                    viewModel.setDefaultCategory(category.code)
                                    showCategoryDialog = false
                                }
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "${category.displayName} (${category.code})",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showCategoryDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun SettingsCard(
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            content()
        }
    }
}