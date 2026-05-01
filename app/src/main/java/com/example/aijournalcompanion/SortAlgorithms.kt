package com.example.aijournalcompanion

/**
 * SortAlgorithms contains custom implementations of sorting logic.
 */
object SortAlgorithms {

    // Sort by Date: Oldest entries first
    fun bubbleSort(list: MutableList<JournalEntry>) {
        val n = list.size
        for (i in 0 until n - 1) {
            for (j in 0 until n - i - 1) {
                // If current is AFTER next, swap them (Ascending)
                if (list[j].date.after(list[j + 1].date)) {
                    val temp = list[j]
                    list[j] = list[j + 1]
                    list[j + 1] = temp
                }
            }
        }
    }

    // Sort by Emotion: Alphabetical (A-Z)
    fun insertionSort(list: MutableList<JournalEntry>) {
        for (i in 1 until list.size) {
            val key = list[i]
            var j = i - 1
            // Compare emotion strings
            val keyEmotion = key.emotion ?: ""
            while (j >= 0 && (list[j].emotion ?: "") > keyEmotion) {
                list[j + 1] = list[j]
                j--
            }
            list[j + 1] = key
        }
    }

    // Sort by Date: Newest entries first
    fun selectionSort(list: MutableList<JournalEntry>) {
        val n = list.size
        for (i in 0 until n - 1) {
            var latestIdx = i
            for (j in i + 1 until n) {
                if (list[j].date.after(list[latestIdx].date)) {
                    latestIdx = j
                }
            }
            val temp = list[latestIdx]
            list[latestIdx] = list[i]
            list[i] = temp
        }
    }
}
