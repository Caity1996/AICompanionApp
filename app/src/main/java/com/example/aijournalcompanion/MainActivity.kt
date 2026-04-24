package com.example.aijournalcompanion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.aijournalcompanion.ui.theme.AIJournalCompanionTheme
import androidx.compose.runtime.getValue
import androidx.activity.compose.BackHandler

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AIJournalCompanionTheme {
                var currentScreen by remember { mutableStateOf(AppScreen.MENU) }

                when (currentScreen) {
                    AppScreen.MENU -> {
                        MainMenu { nextScreen -> currentScreen = nextScreen }
                    }

                    AppScreen.JOURNAL -> {
                        BackHandler { currentScreen = AppScreen.MENU }
                        JournalScreen()
                    }

                    AppScreen.HISTORY -> {
                        BackHandler { currentScreen = AppScreen.MENU }
                        HistoryScreen()
                    }

                    AppScreen.INSIGHTS -> {
                        BackHandler { currentScreen = AppScreen.MENU }
                        InsightsScreen()
                    }
                }
            }
        }
    }
}
