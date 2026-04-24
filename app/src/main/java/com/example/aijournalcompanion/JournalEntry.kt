package com.example.aijournalcompanion

import java.util.Date
import java.util.UUID

data class JournalEntry(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val date: Date = Date(),
    val emotion: String? = null,
    val advice: String? = null
)
