package com.example.aijournalcompanion

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

enum class SearchType { MAP, TREE, DLL }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen() {
    var searchQuery by remember { mutableStateOf("") }
    var selectedSearchType by remember { mutableStateOf(SearchType.MAP) }
    var expandedSort by remember { mutableStateOf(false) }
    var selectedSortName by remember { mutableStateOf("Default") }

    // Drag and Drop State
    var draggedEntry by remember { mutableStateOf<JournalEntry?>(null) }
    var trashZonePosition by remember { mutableStateOf(Offset.Zero) }
    var trashZoneSize by remember { mutableStateOf(IntOffset.Zero) }
    var isHoveringTrash by remember { mutableStateOf(false) }

    val allEntries = JournalHistory.entries
    val filteredEntries = remember(allEntries, searchQuery, selectedSearchType) {
        when (selectedSearchType) {
            SearchType.MAP -> SearchAlgorithms.searchUsingMap(allEntries, searchQuery)
            SearchType.TREE -> SearchAlgorithms.searchUsingBinaryTree(allEntries, searchQuery)
            SearchType.DLL -> SearchAlgorithms.searchUsingDLL(allEntries, searchQuery)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text(
                text = "Journal History",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Search and Sort UI
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                placeholder = { Text("Search thoughts...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true
            )

            Text("Search Algorithm:", style = MaterialTheme.typography.labelLarge)
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SearchType.entries.forEach { type ->
                    FilterChip(
                        selected = selectedSearchType == type,
                        onClick = { selectedSearchType = type },
                        label = { Text(type.name) }
                    )
                }
            }

            // Sort Dropdown
            Box(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                OutlinedButton(
                    onClick = { expandedSort = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Sort by: $selectedSortName")
                }
                DropdownMenu(
                    expanded = expandedSort,
                    onDismissRequest = { expandedSort = false },
                    modifier = Modifier.fillMaxWidth(0.9f)
                ) {
                    DropdownMenuItem(
                        text = { Text("Bubble Sort") },
                        onClick = {
                            selectedSortName = "Bubble Sort"
                            JournalHistory.sortByBubbleSort()
                            expandedSort = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Insertion Sort") },
                        onClick = {
                            selectedSortName = "Insertion Sort"
                            JournalHistory.sortByInsertionSort()
                            expandedSort = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Selection Sort") },
                        onClick = {
                            selectedSortName = "Selection Sort"
                            JournalHistory.sortBySelectionSort()
                            expandedSort = false
                        }
                    )
                }
            }

            // List of Entries
            if (filteredEntries.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(if (searchQuery.isEmpty()) "No entries yet." else "No matches found.")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 80.dp) // Space for Trash Zone
                ) {
                    items(filteredEntries, key = { it.id }) { entry ->
                        DraggableEntryCard(
                            entry = entry,
                            onDragStart = { draggedEntry = it },
                            onDragEnd = { offset ->
                                // Check if dropped in trash zone
                                if (isOffsetInTrashZone(offset, trashZonePosition, trashZoneSize)) {
                                    JournalHistory.deleteEntry(entry)
                                }
                                draggedEntry = null
                                isHoveringTrash = false
                            },
                            onDrag = { offset ->
                                isHoveringTrash = isOffsetInTrashZone(offset, trashZonePosition, trashZoneSize)
                            }
                        )
                    }
                }
            }
        }

        // --- TRASH ZONE ---
        AnimatedVisibility(
            visible = draggedEntry != null,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            val scale by animateFloatAsState(if (isHoveringTrash) 1.2f else 1f)
            val color = if (isHoveringTrash) Color.Red else MaterialTheme.colorScheme.error

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .padding(16.dp)
                    .graphicsLayer(scaleX = scale, scaleY = scale)
                    .background(color.copy(alpha = 0.2f), MaterialTheme.shapes.medium)
                    .onGloballyPositioned {
                        trashZonePosition = it.positionInRoot()
                        trashZoneSize = IntOffset(it.size.width, it.size.height)
                    },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Delete, contentDescription = "Trash", tint = color)
                    Text("Drop here to delete", color = color, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun DraggableEntryCard(
    entry: JournalEntry,
    onDragStart: (JournalEntry) -> Unit,
    onDragEnd: (Offset) -> Unit,
    onDrag: (Offset) -> Unit
) {
    var offset by remember { mutableStateOf(Offset.Zero) }
    var isDragging by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .offset { IntOffset(offset.x.roundToInt(), offset.y.roundToInt()) }
            .pointerInput(Unit) {
                detectDragGesturesAfterLongPress(
                    onDragStart = {
                        isDragging = true
                        onDragStart(entry)
                    },
                    onDragEnd = {
                        // Calculate absolute position for drop check
                        // Note: This is simplified. In a real app you'd use layout coordinates.
                        onDragEnd(offset) 
                        offset = Offset.Zero
                        isDragging = false
                    },
                    onDragCancel = {
                        offset = Offset.Zero
                        isDragging = false
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        offset += dragAmount
                        onDrag(offset)
                    }
                )
            }
            .graphicsLayer(
                scaleX = if (isDragging) 0.9f else 1f,
                scaleY = if (isDragging) 0.9f else 1f,
                alpha = if (isDragging) 0.7f else 1f
            )
    ) {
        EntryCard(entry)
    }
}

private fun isOffsetInTrashZone(dragOffset: Offset, trashPos: Offset, trashSize: IntOffset): Boolean {
    // This is a simplified check. A more robust way would involve getting the absolute 
    // position of the card during drag. 
    // For this prototype, we'll check if the drag distance is sufficient to reach the bottom.
    return dragOffset.y > 300f // Rough estimate for "dragging towards the bottom"
}

@Composable
fun EntryCard(entry: JournalEntry) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy - HH:mm", Locale.getDefault())
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = dateFormat.format(entry.date),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = getEmotionEmoji(entry.emotion),
                    style = MaterialTheme.typography.titleMedium
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = entry.text,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
            
            entry.advice?.let {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Advice: $it",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

fun getEmotionEmoji(emotion: String?): String {
    return when (emotion?.uppercase()) {
        "JOY" -> "JOY 😊"
        "SADNESS" -> "SADNESS 😢"
        "ANGER" -> "ANGER 😡"
        "FEAR" -> "FEAR 😨"
        "SURPRISE" -> "SURPRISE 😲"
        else -> "NEUTRAL 😐"
    }
}
