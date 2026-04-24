package com.example.aijournalcompanion.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aijournalcompanion.JournalEntry
import com.example.aijournalcompanion.JournalHistory
import com.example.aijournalcompanion.network.AnalyzeRequest
import com.example.aijournalcompanion.network.RetrofitClient
import kotlinx.coroutines.launch

class JournalViewModel : ViewModel() {

    var journalText by mutableStateOf("")
    var isAnalyzing by mutableStateOf(false)
    var analysisResult by mutableStateOf<JournalEntry?>(null)
    var errorMessage by mutableStateOf<String?>(null)

    fun analyzeJournal() {
        if (journalText.isBlank()) return

        isAnalyzing = true
        errorMessage = null
        
        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.analyzeEntry(AnalyzeRequest(journalText))
                val newEntry = JournalEntry(
                    text = journalText,
                    emotion = response.emotion,
                    advice = response.advice
                )
                
                analysisResult = newEntry
                
                // SAVE TO HISTORY
                JournalHistory.addEntry(newEntry)

            } catch (e: Exception) {
                errorMessage = "Failed to reach AI: ${e.message}"
            } finally {
                isAnalyzing = false
            }
        }
    }
    
    fun reset() {
        journalText = ""
        analysisResult = null
        errorMessage = null
    }
}
