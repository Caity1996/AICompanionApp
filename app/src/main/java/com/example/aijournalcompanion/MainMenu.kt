package com.example.aijournalcompanion

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainMenu(onNavigate: (AppScreen) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding() // Pushes content below the camera/status bar
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "AI Journal Companion",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        MenuButtonWithTooltip(
            text = "New Journal Entry",
            tooltip = "Start a new daily reflection and get AI feedback.",
            onClick = { onNavigate(AppScreen.JOURNAL) }
        )

        MenuButtonWithTooltip(
            text = "View History",
            tooltip = "Access, sort, and search your past journal entries.",
            onClick = { onNavigate(AppScreen.HISTORY) }
        )

        MenuButtonWithTooltip(
            text = "Emotion Insights",
            tooltip = "Visualize your emotional trends over time with a pie chart.",
            onClick = { onNavigate(AppScreen.INSIGHTS) }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuButtonWithTooltip(text: String, tooltip: String, onClick: () -> Unit) {
    val tooltipState = rememberTooltipState(isPersistent = false)

    TooltipBox(
        positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
        tooltip = {
            PlainTooltip {
                Text(tooltip)
            }
        },
        state = tooltipState
    ) {
        Button(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        ) {
            Text(text)
        }
    }
}
