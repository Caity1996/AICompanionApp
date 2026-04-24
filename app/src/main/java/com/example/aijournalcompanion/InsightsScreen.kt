package com.example.aijournalcompanion

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun InsightsScreen() {
    val entries = JournalHistory.entries
    
    // 1. Group and count emotions
    val emotionCounts = entries.groupBy { it.emotion?.uppercase() ?: "NEUTRAL" }
        .mapValues { it.value.size }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Emotion Distribution",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        if (entries.isEmpty()) {
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                Text("No data available yet. Write some journals!")
            }
        } else {
            // 2. Draw the Pie Chart
            Box(
                modifier = Modifier
                    .size(250.dp)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                EmotionPieChart(emotionCounts)
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 3. Legend
            Text("Legend", style = MaterialTheme.typography.titleMedium, modifier = Modifier.align(Alignment.Start))
            Spacer(modifier = Modifier.height(8.dp))
            
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(emotionCounts.keys.toList()) { emotion ->
                    LegendItem(emotion, emotionCounts[emotion] ?: 0, entries.size)
                }
            }
        }
    }
}

@Composable
fun EmotionPieChart(data: Map<String, Int>) {
    val total = data.values.sum().toFloat()

    Canvas(modifier = Modifier.fillMaxSize()) {
        var startAngle = -90f // Start from the top
        
        data.entries.forEach { entry ->
            val sweepAngle = (entry.value / total) * 360f
            drawArc(
                color = getEmotionColor(entry.key),
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = true,
                size = Size(size.width, size.height)
            )
            startAngle += sweepAngle
        }
    }
}

@Composable
fun LegendItem(emotion: String, count: Int, total: Int) {
    val percentage = (count.toFloat() / total * 100).toInt()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .background(getEmotionColor(emotion), shape = MaterialTheme.shapes.extraSmall)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = emotion, modifier = Modifier.weight(1f))
        Text(text = "$count entries ($percentage%)", fontWeight = FontWeight.Bold)
    }
}

fun getEmotionColor(emotion: String): Color {
    return when (emotion.uppercase()) {
        "JOY" -> Color(0xFFFFD700)
        "SADNESS" -> Color(0xFF6495ED)
        "ANGER" -> Color(0xFFFF4500)
        "FEAR" -> Color(0xFF9370DB)
        "SURPRISE" -> Color(0xFF00FA9A)
        else -> Color(0xFF808080)
    }
}
