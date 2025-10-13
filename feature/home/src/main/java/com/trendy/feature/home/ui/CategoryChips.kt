package com.trendy.feature.home.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CategoryChips(
    categories: List<String>,
    selected: String,
    onSelect: (String) -> Unit
) {
    LazyRow(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        items(categories.size) { index ->
            val cat = categories[index]
            AssistChip(
                onClick = { onSelect(cat) },
                label = { Text(cat) },
                modifier = Modifier.padding(end = 8.dp),
                leadingIcon = null
            )
        }
    }
}