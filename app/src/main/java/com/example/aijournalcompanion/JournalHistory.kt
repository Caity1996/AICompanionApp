package com.example.aijournalcompanion

import androidx.compose.runtime.mutableStateListOf

/**
 * JournalHistory manages the collection of journal entries.
 */
object JournalHistory {
    private val _entries = mutableStateListOf<JournalEntry>()
    val entries: List<JournalEntry> get() = _entries

    fun addEntry(entry: JournalEntry) {
        _entries.add(0, entry)
    }

    fun deleteEntry(entry: JournalEntry) {
        _entries.remove(entry)
    }

    fun sortByBubbleSort() {
        SortAlgorithms.bubbleSort(_entries)
    }

    fun sortByInsertionSort() {
        SortAlgorithms.insertionSort(_entries)
    }

    fun sortBySelectionSort() {
        SortAlgorithms.selectionSort(_entries)
    }
}
