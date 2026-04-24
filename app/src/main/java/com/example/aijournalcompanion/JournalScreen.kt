package com.example.aijournalcompanion

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.aijournalcompanion.ui.JournalViewModel

@Composable
fun JournalScreen(viewModel: JournalViewModel = viewModel()) {
    // Reset the screen state whenever this composable enters the composition
    LaunchedEffect(Unit) {
        viewModel.reset()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "How are you feeling today?",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = viewModel.journalText,
            onValueChange = { viewModel.journalText = it },
            label = { Text("Your thoughts...") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            minLines = 5,
            enabled = !viewModel.isAnalyzing
        )

        if (viewModel.isAnalyzing) {
            CircularProgressIndicator(modifier = Modifier.padding(16.dp))
            Text("AI is reflecting on your thoughts...")
        } else {
            Button(
                onClick = { viewModel.analyzeJournal() },
                modifier = Modifier.fillMaxWidth(),
                enabled = viewModel.journalText.isNotBlank()
            ) {
                Text("Analyze Emotion")
            }
        }

        // Display the AI Result
        viewModel.analysisResult?.let { result ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Dominant Emotion: ${result.emotion}",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = result.advice ?: "",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }

        // Display Error if any
        viewModel.errorMessage?.let { error ->
            Text(
                text = error,
                color = Color.Red,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}
