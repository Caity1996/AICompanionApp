package com.example.aijournalcompanion

/**
 * SearchAlgorithms contains custom implementations of searching logic
 * using different data structures as per requirements.
 */
object SearchAlgorithms {

    // --- 1. HASH-BASED (MAP) SEARCH ---
    // High performance keyword lookup using a Map.
    fun searchUsingMap(entries: List<JournalEntry>, query: String): List<JournalEntry> {
        if (query.isBlank()) return entries
        
        val keywordMap = mutableMapOf<String, MutableList<JournalEntry>>()
        for (entry in entries) {
            val words = entry.text.lowercase().split(Regex("\\W+"))
            for (word in words) {
                if (word.isNotEmpty()) {
                    keywordMap.getOrPut(word) { mutableListOf() }.add(entry)
                }
            }
        }
        // Returns entries that contain the specific word (exact word match)
        return keywordMap[query.lowercase()]?.distinct() ?: emptyList()
    }

    // --- 2. BINARY TREE SEARCH ---
    class TreeNode(val entry: JournalEntry) {
        var left: TreeNode? = null
        var right: TreeNode? = null
    }

    fun searchUsingBinaryTree(entries: List<JournalEntry>, query: String): List<JournalEntry> {
        if (query.isBlank()) return entries
        if (entries.isEmpty()) return emptyList()

        // Build the BST (sorted by text)
        var root: TreeNode? = null
        for (entry in entries) {
            root = insertIntoTree(root, entry)
        }

        val results = mutableListOf<JournalEntry>()
        traverseAndSearch(root, query.lowercase(), results)
        return results
    }

    private fun insertIntoTree(root: TreeNode?, entry: JournalEntry): TreeNode {
        if (root == null) return TreeNode(entry)
        if (entry.text.lowercase() < root.entry.text.lowercase()) {
            root.left = insertIntoTree(root.left, entry)
        } else {
            root.right = insertIntoTree(root.right, entry)
        }
        return root
    }

    private fun traverseAndSearch(node: TreeNode?, query: String, results: MutableList<JournalEntry>) {
        if (node == null) return
        if (node.entry.text.lowercase().contains(query)) {
            results.add(node.entry)
        }
        traverseAndSearch(node.left, query, results)
        traverseAndSearch(node.right, query, results)
    }

    // --- 3. DOUBLY LINKED LIST SEARCH ---
    class DLLNode(val entry: JournalEntry) {
        var next: DLLNode? = null
        var prev: DLLNode? = null
    }

    fun searchUsingDLL(entries: List<JournalEntry>, query: String): List<JournalEntry> {
        if (query.isBlank()) return entries
        if (entries.isEmpty()) return emptyList()

        // Build the Doubly Linked List
        val head = DLLNode(entries[0])
        var current = head
        for (i in 1 until entries.size) {
            val newNode = DLLNode(entries[i])
            current.next = newNode
            newNode.prev = current
            current = newNode
        }

        // Search by traversing the list
        val results = mutableListOf<JournalEntry>()
        var searchNode: DLLNode? = head
        while (searchNode != null) {
            if (searchNode.entry.text.lowercase().contains(query.lowercase())) {
                results.add(searchNode.entry)
            }
            searchNode = searchNode.next
        }
        return results
    }
}
