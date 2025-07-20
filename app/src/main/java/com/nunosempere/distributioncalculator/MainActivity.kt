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

import com.nunosempere.distributioncalculator.samples.*
import com.nunosempere.distributioncalculator.ui.theme.DistributionCalculatorTheme

object Routes {
    const val CALCULATOR = "calculator"
    const val TIPS = "tips"
    const val HISTORY = "history"
}

data class CalculatorState(
    val output: Distribution = Distribution.Lognormal(low = 1.0, high = 1.0),
    val outputTagLow: Double = 1.0,
    val outputTagHigh: Double = 1.0,
    val inputFieldLow: Double = 0.0,
    val inputFieldHigh: Double = 0.0,
    val operation: String = "×",
    val selectedInput: Int = 0,
    val onDecimalInput: Int = 0,
    val onDecimalLevel: Int = -1,
    val isSwipeProcessing: Boolean = false,
    val showMoreOptionsMenu: Boolean = false
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            var history by remember { mutableStateOf("1 to 1 // initial state") }
            var cs_shared by remember { mutableStateOf(CalculatorState()) }
            DistributionCalculatorTheme {
                NavHost(
                    navController = navController,
                    startDestination = Routes.CALCULATOR
                ) {
                    composable(Routes.CALCULATOR) {
                        Calculator(
                            history = history,
                            onHistoryUpdate = { newHistory -> history = newHistory },
                            cs_shared = cs_shared,
                            onCsSharedUpdate = { new_cs -> cs_shared = new_cs },
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
