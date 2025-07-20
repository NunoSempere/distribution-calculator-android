package com.nunosempere.distributioncalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import com.nunosempere.distributioncalculator.ui.theme.DistributionCalculatorTheme

object Routes {
    const val CALCULATOR = "calculator"
    const val TIPS = "tips"
    const val HISTORY = "history"
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            var history by remember { mutableStateOf("1 to 1 // initial state") }
            DistributionCalculatorTheme {
                NavHost(
                    navController = navController,
                    startDestination = Routes.CALCULATOR
                ) {
                    composable(Routes.CALCULATOR) {
                        Calculator(
                            history = history,
                            onHistoryUpdate = { newHistory -> history = newHistory },
                            onNavigateToTips = {
                                navController.navigate(Routes.TIPS)
                            },
                            onNavigateToHistory = {
                                navController.navigate(Routes.HISTORY)
                            }
                        )
                    }
                    composable(Routes.TIPS) {
                        TipsScreen(
                            onBack = {
                                navController.popBackStack()
                            }
                        )
                    }
                    composable(Routes.HISTORY) {
                        HistoryScreen(
                            onBack = {
                                navController.popBackStack()
                            },
                            history = history
                        )
                    }
                }
            }
        }
    }
}
