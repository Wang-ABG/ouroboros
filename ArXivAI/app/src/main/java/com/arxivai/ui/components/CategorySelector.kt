package com.arxivai.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

data class ArXivCategory(
    val code: String,
    val displayName: String
)

val arXivCategories = listOf(
    ArXivCategory("cs.AI", "AI"),
    ArXivCategory("cs.LG", "ML"),
    ArXivCategory("cs.CV", "CV"),
    ArXivCategory("cs.CL", "NLP"),
    ArXivCategory("cs.RO", "Robotics"),
    ArXivCategory("cs.NE", "Neural"),
    ArXivCategory("cs.IR", "IR"),
    ArXivCategory("cs.SE", "SE"),
    ArXivCategory("cs.CR", "Security"),
    ArXivCategory("cs.DS", "Data Sci"),
    ArXivCategory("cs.DB", "Database"),
    ArXivCategory("cs.PL", "PL"),
    ArXivCategory("stat.ML", "Stats"),
    ArXivCategory("math.OC", "Optim"),
    ArXivCategory("q-bio", "Bio"),
    ArXivCategory("q-fin", "Finance"),
    ArXivCategory("physics", "Physics"),
    ArXivCategory("math", "Math"),
    ArXivCategory("eess", "EESS"),
    ArXivCategory("econ", "Econ")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategorySelector(
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // "All" option
        FilterChip(
            selected = selectedCategory.isEmpty(),
            onClick = { onCategorySelected("") },
            label = { Text("All", style = MaterialTheme.typography.labelMedium) },
            shape = RoundedCornerShape(20.dp),
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.primary,
                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
            )
        )

        arXivCategories.forEach { category ->
            FilterChip(
                selected = selectedCategory == category.code,
                onClick = { onCategorySelected(category.code) },
                label = { Text(category.displayName, style = MaterialTheme.typography.labelMedium) },
                shape = RoundedCornerShape(20.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    }
}