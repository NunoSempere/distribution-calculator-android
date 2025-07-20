package com.nunosempere.distributioncalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.SnackbarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import com.nunosempere.distributioncalculator.ui.theme.CommandColor
import com.nunosempere.distributioncalculator.ui.theme.DistributionCalculatorTheme
import com.nunosempere.distributioncalculator.ui.theme.EqualColor
import com.nunosempere.distributioncalculator.ui.theme.NumberColor
import com.nunosempere.distributioncalculator.ui.theme.OperationColor
import com.nunosempere.distributioncalculator.ui.theme.OperationSelectedColor
import com.nunosempere.distributioncalculator.ui.theme.UnitColor
import com.nunosempere.distributioncalculator.ui.theme.SurfaceVariantSelected
import com.nunosempere.distributioncalculator.ui.percentileindicator.*
import com.nunosempere.distributioncalculator.samples.*

import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.min
import kotlin.math.max
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon

object Routes {
    const val CALCULATOR = "calculator"
    const val TIPS = "tips"
    const val HISTORY = "history"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TipsScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tips") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
			Text("■ The boxes represent the 90% confidence interval (5% to 95%) of your distributions")
			Text("■ Swipe left or right to duplicate an input")
			Text("")
			Text("Try this example estimating the amount of fat burnt by going down and coming back up from the Grand canyon:")
			Text("300 600, meters going down")
			Text("× 98 105, kgs of weight")
			Text("× 1.3 2, going down, and then going back up the Canyon, but down is easier")
			Text("× 9.8 gravitational constant to get to Jules spent")
			Text("÷ 15% 35%, wild guess on chemical to physical efficiency conversion")
			Text("÷ 70% 90%, wild guess for share of loss that is due to the direct physical exercise")
			Text("(as opposed to higher burn rate, cost of building muscles, etc.)")
			Text("÷ 4, jules to calories")
			Text("÷ 6K to 8K, calories burnt to lose one gram of fat")
			Text("= ¿ to ? grams of fat lost")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(onBack: () -> Unit, history: String) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("History") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(history)
        }
    }
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

@Composable
fun Calculator(
    modifier: Modifier = Modifier,
    history: String = "",
    onHistoryUpdate: (String) -> Unit = {},
    onNavigateToTips: () -> Unit = {},
    onNavigateToHistory: () -> Unit = {}
) {
    var output by remember { mutableStateOf<Distribution>(
        Distribution.Lognormal(low = 1.0, high = 1.0)
    )}
    var output_tag_low by remember { mutableStateOf(1.0) }
    var output_tag_high by remember { mutableStateOf(1.0) }

    var input_field_low by remember { mutableStateOf(0.0) }
    var input_field_high by remember { mutableStateOf(0.0) }

    var operation by remember { mutableStateOf("×") }
    var selected_input by remember { mutableStateOf(0) }
    var on_decimal_input by remember {mutableStateOf(0)}
    var on_decimal_level by remember {mutableStateOf(-1)}
    
    var isSwipeProcessing by remember { mutableStateOf(false) }
    var showMoreOptionsMenu by remember { mutableStateOf(false) }

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val screenHeight = configuration.screenHeightDp
    
    val basePadding = (min(screenWidth, screenHeight) * 0.04f).dp
    val baseSpacing = (min(screenWidth, screenHeight) * 0.02f).dp // Reduced spacing between buttons

    val largeFontSize =  max(min(screenWidth * 0.06f, screenHeight * 0.06f), 20f).sp
    val buttonFontSize = max(min(screenWidth * 0.06f, screenHeight * 0.03f), 18f).sp
    val snackbarFontSize = (screenHeight * 0.035f).sp  // Reduced from 0.05f to 0.035f

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    fun throwSnackbar(error_msg: String) {
            coroutineScope.launch {
                snackbarHostState.showSnackbar(error_msg)
            }
    }

    fun calculateResult(): Distribution {
        val input = Distribution.Lognormal(low = input_field_low, high = input_field_high)
        val result = when (operation) {
            "×" -> MultiplyDists(input, output) 
            "÷" -> DivideDists(output, input)
            "+" -> SumDists(input, output)
            "-" -> SubstractDists(output, input)
            else -> Distribution.Err("Unsupported operation type")
        }
        onHistoryUpdate(history + "\n" + operation + " " + input_field_low + " to " + input_field_high)
        return result
    }

    fun toPrettyString(d: Double): String {
        return when {
            abs(d) >= 1_000_000_000_000.0 -> {
                "%.1fT".format(d / 1_000_000_000_000.0)
            }
            abs(d) >= 1_000_000_000.0 -> {
                "%.1fB".format(d / 1_000_000_000.0)
            }
            abs(d) >= 1_000_000.0 -> {
                "%.1fM".format(d / 1_000_000.0)
            }
            abs(d) >= 1_000.0 -> {
                "%.1fK".format(d / 1_000.0)
            }
            abs(d) <= 0.0001 -> {
                d.toString()
            }
            abs(d) <= 0.001 -> {
                "%.5f".format(d)
            }
            abs(d) <= 0.01 -> {
                "%.4f".format(d)
            }
            abs(d) <= 0.1 -> {
                "%.3f".format(d)
            }
            else -> {
                "%.2f".format(d)
            }
        }
    }

    fun onNumberClick(number: Int) {
        if(selected_input == 0){
            if(on_decimal_input == 0){
                input_field_low = input_field_low * 10 + number
            } else {
                input_field_low = input_field_low + number * 10.0.pow(on_decimal_level)
                on_decimal_level = on_decimal_level - 1
            }
            input_field_high = max(input_field_low, input_field_high)
        } else {
            if(on_decimal_input == 0){
                input_field_high = input_field_high * 10 + number
            } else {
                input_field_high = input_field_high + number * 10.0.pow(on_decimal_level)
                on_decimal_level = on_decimal_level - 1
            }
            // input_field_low = min(input_field_low, input_field_high)
        }
        /*
        if (on_decimal_input == 0) {
            if (selected_input == 0) {
                input_field_low = input_field_low * 10 + number
                input_field_high = max(input_field_low, input_field_high)
            } else {
                input_field_high = input_field_high * 10 + number
                input_field_low = min(input_field_low, input_field_high)
            }
        } else {
            if (selected_input == 0) {
                input_field_low = input_field_low + number * 10.0.pow(on_decimal_level)
            } else {
                input_field_high = input_field_high + number * 10.0.pow(on_decimal_level)
            }
            on_decimal_level = on_decimal_level - 1
        }
        */
    }

    fun onOperationClick(op: String) {
        operation = op
        // throwSnackbar(op)
        on_decimal_input = 0
        on_decimal_level = -1
    }

    fun onMultiplierClick(multiplier: String) {
        if (selected_input == 0) {
            input_field_low = when (multiplier) {
                "%" -> input_field_low * 0.01
                "K" -> input_field_low * 1000.0
                "M" -> input_field_low * 1000.0 * 1000.0
                "B" -> input_field_low * 1000.0 * 1000.0 * 1000.0
                "T" -> input_field_low * 1000.0 * 1000.0 * 1000.0 * 1000.0
                else -> input_field_low
            }
            input_field_high = max(input_field_low, input_field_high)
            selected_input = 1
        } else {
            input_field_high = when (multiplier) {
                "%" -> input_field_high * 0.01
                "K" -> input_field_high * 1000.0
                "M" -> input_field_high * 1000.0 * 1000.0
                "B" -> input_field_high * 1000.0 * 1000.0 * 1000.0
                "T" -> input_field_high * 1000.0 * 1000.0 * 1000.0 * 1000.0
                else -> input_field_high
            }
        }
        on_decimal_input = 0
        on_decimal_level = -1
    }

    fun onEqualsClick() {
        if (input_field_low > input_field_high) {
            throwSnackbar("Error: first field must be lower than second")
            return
        } else if (input_field_low == 0.0) {
            throwSnackbar("Error: first field can't be zero yet. If this is important to you, tell Nuño")
            return
        } else if (input_field_high == 0.0 ){
            throwSnackbar("Error: second field can't be zero yet. If this is important to you, tell Nuño")
            return
        }
        
        // throwSnackbar(operation)
        val result = calculateResult()
        when(result) {
            is Distribution.Lognormal -> {
                output_tag_low = result.low
                output_tag_high = result.high

                output = result
            }
            is Distribution.SamplesArray -> {
                val xs = (result.samples).copyOf()
                xs.sort()
                output_tag_low = xs[5_000]
                output_tag_high = xs[95_000]

                output = result
            }
            is Distribution.Err -> {
                throwSnackbar(result.msg)
            }
        }

        input_field_low = 0.0
        input_field_high = 0.0
        operation = "×" 
        selected_input = 0
        on_decimal_input = 0
        on_decimal_level = -1
    }

    fun onRestartClick() {
        output = Distribution.Lognormal(low = 1.0, high = 1.0)
        output_tag_low = 1.0
        output_tag_high = 1.0

        input_field_low = 0.0
        input_field_high = 0.0
        operation = "×" 
        selected_input = 0
        on_decimal_input = 0
        on_decimal_level = -1
        
        onHistoryUpdate("1 to 1 // initial state")
    }

    fun onClearClick() {
        if (selected_input == 0) {
            input_field_low = 0.0
        } else {
            input_field_high = 0.0
        }
        on_decimal_input = 0
        on_decimal_level = -1
    }

    fun onDecimalClick() {
        on_decimal_input = 1
    }

    fun handleSwipe(direction: SwipeDirection) {
        if (!isSwipeProcessing) {
            isSwipeProcessing = true
            
            when (direction) {
                SwipeDirection.LEFT -> {
                    input_field_low = input_field_high
                    // Handle left swipe
                    // throwSnackbar("Swiped left")
                    // Additional actions for left swipe can be added here
                }
                SwipeDirection.RIGHT -> {
                    input_field_high = input_field_low
                    // Handle right swipe
                    // throwSnackbar("Swiped right")
                    // Additional actions for right swipe can be added here
                }
            }
            
            // Reset the processing flag after a delay
            coroutineScope.launch {
                delay(500) // 500ms debounce time
                isSwipeProcessing = false
            }
        }
    }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val availableHeight = maxHeight
        // val availableWidth = maxWidth
        
        val outputBoxHeight = availableHeight * 0.12f
        val inputBoxHeight = availableHeight * 0.12f

        Scaffold(
            snackbarHost = { 
                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier.padding(16.dp)
                ) { data ->
                    androidx.compose.material3.Snackbar(
                        modifier = Modifier.padding(16.dp),
                        containerColor = SnackbarDefaults.color,
                        contentColor = SnackbarDefaults.contentColor
                    ) {
                        Text(
                            text = data.visuals.message,
                            fontSize = snackbarFontSize,
                        )
                    }
                }
            }
        ) { innerPadding -> 
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(all = basePadding),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(baseSpacing)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(baseSpacing)
                    ) {
                        Box(
                            modifier = Modifier
                                .height(height = outputBoxHeight)
                                .weight(1f)
                                .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(8.dp))
                                .padding(all = basePadding/2),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopCenter)
                                    .offset(y = (-30).dp, x = (+32).dp)
                                    .zIndex(1f)
                            ) {
                                PercentileIndicator(text = "5%", blue = true)
                            }
                            Text(
                                text = toPrettyString(output_tag_low),
                                fontSize = largeFontSize,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.End,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                maxLines = 1
                            )
                        }
                        Box(
                            modifier = Modifier
                                .height(height = outputBoxHeight)
                                .weight(1f)
                                .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(8.dp))
                                .padding(all = basePadding/2),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopCenter)
                                    .offset(y = (-30).dp, x = (+32).dp)
                                    .zIndex(1f)
                            ) {
                                PercentileIndicator(text = "95%", blue = true)
                            }
                            Text(
                                text = toPrettyString(output_tag_high),
                                fontSize = largeFontSize,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.End,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                maxLines = 1
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(height = baseSpacing))

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(baseSpacing)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(baseSpacing)
                    ) {
                        ResponsiveCalculatorButton(
                            text = "×",
                            onClick = { onOperationClick("×") },
                            modifier = Modifier.fillMaxWidth().weight(1f),
                            buttonType = ButtonType.OPERATION,
                            fontSize = buttonFontSize,
                            isSelected = operation == "×"
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(baseSpacing)
                    ) {
                        ResponsiveCalculatorButton(
                            text = "+",
                            onClick = { onOperationClick("+") },
                            modifier = Modifier.weight(1f),
                            buttonType = ButtonType.OPERATION,
                            fontSize = buttonFontSize,
                            isSelected = operation == "+"
                        )
                        ResponsiveCalculatorButton(
                            text = "÷",
                            onClick = { onOperationClick("÷") },
                            modifier = Modifier.weight(1f),
                            buttonType = ButtonType.OPERATION,
                            fontSize = buttonFontSize,
                            isSelected = operation == "÷"
                        )
                        ResponsiveCalculatorButton(
                            text = "-",
                            onClick = { onOperationClick("-") },
                            modifier = Modifier.weight(1f),
                            buttonType = ButtonType.OPERATION,
                            fontSize = buttonFontSize,
                            isSelected = operation == "-"
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(baseSpacing)
                    ) {
                        ResponsiveCalculatorButton(
                            text = "1",
                            onClick = { onNumberClick(1) },
                            modifier = Modifier.weight(1f),
                            buttonType = ButtonType.NUMBER,
                            fontSize = buttonFontSize
                        )
                        ResponsiveCalculatorButton(
                            text = "2",
                            onClick = { onNumberClick(2) },
                            modifier = Modifier.weight(1f),
                            buttonType = ButtonType.NUMBER,
                            fontSize = buttonFontSize
                        )
                        ResponsiveCalculatorButton(
                            text = "3",
                            onClick = { onNumberClick(3) },
                            modifier = Modifier.weight(1f),
                            buttonType = ButtonType.NUMBER,
                            fontSize = buttonFontSize
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(baseSpacing)
                    ) {
                        ResponsiveCalculatorButton(
                            text = "4",
                            onClick = { onNumberClick(4) },
                            modifier = Modifier.weight(1f),
                            buttonType = ButtonType.NUMBER,
                            fontSize = buttonFontSize
                        )
                        ResponsiveCalculatorButton(
                            text = "5",
                            onClick = { onNumberClick(5) },
                            modifier = Modifier.weight(1f),
                            buttonType = ButtonType.NUMBER,
                            fontSize = buttonFontSize
                        )
                        ResponsiveCalculatorButton(
                            text = "6",
                            onClick = { onNumberClick(6) },
                            modifier = Modifier.weight(1f),
                            buttonType = ButtonType.NUMBER,
                            fontSize = buttonFontSize
                        )
                    }
                    
                    Row(
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(baseSpacing)
                    ) {
                        ResponsiveCalculatorButton(
                            text = "7",
                            onClick = { onNumberClick(7) },
                            modifier = Modifier.weight(1f),
                            buttonType = ButtonType.NUMBER,
                            fontSize = buttonFontSize
                        )
                        ResponsiveCalculatorButton(
                            text = "8",
                            onClick = { onNumberClick(8) },
                            modifier = Modifier.weight(1f),
                            buttonType = ButtonType.NUMBER,
                            fontSize = buttonFontSize
                        )
                        ResponsiveCalculatorButton(
                            text = "9",
                            onClick = { onNumberClick(9) },
                            modifier = Modifier.weight(1f),
                            buttonType = ButtonType.NUMBER,
                            fontSize = buttonFontSize
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(baseSpacing)
                    ) {
                        ResponsiveCalculatorButton(
                            text = "↻",
                            onClick = { onRestartClick() },
                            modifier = Modifier.weight(1f),
                            buttonType = ButtonType.COMMAND,
                            fontSize = buttonFontSize
                        )
                        ResponsiveCalculatorButton(
                            text = "0",
                            onClick = { onNumberClick(0) },
                            modifier = Modifier.weight(1f),
                            buttonType = ButtonType.NUMBER,
                            fontSize = buttonFontSize
                        )
                        ResponsiveCalculatorButton(
                            text = ".",
                            onClick = { onDecimalClick() },
                            modifier = Modifier.weight(1f),
                            buttonType = ButtonType.NUMBER,
                            fontSize = buttonFontSize
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(baseSpacing)
                    ) {
                        Box(
                            modifier = Modifier.weight(1f)
                        ) {
                            ResponsiveCalculatorButton(
                                text = "...",
                                onClick = { showMoreOptionsMenu = true },
                                modifier = Modifier.fillMaxWidth().fillMaxHeight(),
                                buttonType = ButtonType.COMMAND,
                                fontSize = buttonFontSize
                            )
                            DropdownMenu(
                                expanded = showMoreOptionsMenu,
                                onDismissRequest = { showMoreOptionsMenu = false },
                                modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Tips") },
                                    onClick = {
                                        showMoreOptionsMenu = false
                                        onNavigateToTips()
                                    },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Default.Info,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("History") },
                                    onClick = {
                                        onNavigateToHistory()
                                        showMoreOptionsMenu = false
                                    },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Default.Check,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Option Y ") },
                                    onClick = {
                                        throwSnackbar("Selected Option Y")
                                        showMoreOptionsMenu = false
                                    },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Default.Settings,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Option Z ") },
                                    onClick = {
                                        throwSnackbar("Selected Option Z")
                                        showMoreOptionsMenu = false
                                    },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Default.Info,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                )
                            }
                        }
                        ResponsiveCalculatorButton(
                            text = "K",
                            onClick = { onMultiplierClick("K") },
                            modifier = Modifier.weight(1f),
                            buttonType = ButtonType.UNIT,
                            fontSize = buttonFontSize
                        )
                        ResponsiveCalculatorButton(
                            text = "%",
                            onClick = { onMultiplierClick("%") },
                            modifier = Modifier.weight(1f),
                            buttonType = ButtonType.UNIT,
                            fontSize = buttonFontSize
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(baseSpacing)
                    ) {
                        ResponsiveCalculatorButton(
                            text = "M",
                            onClick = { onMultiplierClick("M") },
                            modifier = Modifier.weight(1f),
                            buttonType = ButtonType.UNIT,
                            fontSize = buttonFontSize
                        )
                        ResponsiveCalculatorButton(
                            text = "B",
                            onClick = { onMultiplierClick("B") },
                            modifier = Modifier.weight(1f),
                            buttonType = ButtonType.UNIT,
                            fontSize = buttonFontSize
                        )
                        ResponsiveCalculatorButton(
                            text = "T",
                            onClick = { onMultiplierClick("T") },
                            modifier = Modifier.weight(1f),
                            buttonType = ButtonType.UNIT,
                            fontSize = buttonFontSize
                        )
                    }
                    
                    Row(
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(baseSpacing)
                    ) {
                        ResponsiveCalculatorButton(
                            text = "=",
                            onClick = { onEqualsClick() },
                            modifier = Modifier.fillMaxWidth(),
                            buttonType = ButtonType.EQUAL,
                            fontSize = buttonFontSize
                        )
                    }
                }

                Spacer(modifier = Modifier.height(height = baseSpacing))

                // Input fields row with swipe detection
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .pointerInput(Unit) {
                            detectHorizontalDragGestures(
                                onDragEnd = {
                                    isSwipeProcessing = false
                                },
                                onDragCancel = {
                                    isSwipeProcessing = false
                                },
                                onHorizontalDrag = { _, dragAmount ->
                                    if (abs(dragAmount) > 20) {
                                        when {
                                            dragAmount < 0 -> handleSwipe(SwipeDirection.LEFT)
                                            dragAmount > 0 -> handleSwipe(SwipeDirection.RIGHT)
                                        }
                                    }
                                }
                            )
                        },
                    horizontalArrangement = Arrangement.spacedBy(baseSpacing)
                ) {
                    Box(
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .height(height = inputBoxHeight)
                                .fillMaxWidth()
                                .background(
                                    if (selected_input == 0) MaterialTheme.colorScheme.surfaceVariant else SurfaceVariantSelected,
                                    RectangleShape
                                )
                                .clickable { selected_input = 0; on_decimal_input = 0; on_decimal_level = -1; input_field_low = 0.0 }
                                .padding(all = basePadding/2)
                                .zIndex(1f),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Text(
                                text = toPrettyString(input_field_low),
                                fontSize = largeFontSize,
                                fontWeight = if (selected_input == 0) FontWeight.Bold else FontWeight.Normal,
                                textAlign = TextAlign.End,
                                maxLines = 1
                            )
                        }
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .offset(y = (+12).dp, x = (+32).dp)
                                .zIndex(2f)
                        ) {
                            PercentileIndicator(text = "5%", selected_input = (selected_input == 0))
                        }
                    }
                    Box(
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .height(height = inputBoxHeight)
                                .fillMaxWidth()
                                .background(
                                    if (selected_input == 1) MaterialTheme.colorScheme.surfaceVariant else SurfaceVariantSelected,
                                    RectangleShape
                                )
                                .clickable { selected_input = 1; on_decimal_input = 0; on_decimal_level = -1; input_field_high = 0.0 }
                                .padding(all = basePadding/2)
                                .zIndex(1f),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Text(
                                text = toPrettyString(input_field_high),
                                fontSize = largeFontSize,
                                fontWeight = if (selected_input == 1) FontWeight.Bold else FontWeight.Normal,
                                textAlign = TextAlign.End,
                                maxLines = 1
                            )
                        }
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .offset(y = (+12).dp, x = (+32).dp)
                                .zIndex(2f)
                        ) {
                            PercentileIndicator(text = "95%", selected_input = (selected_input == 1))
                        }
                    }
                }
            }
        }
    }
}

enum class ButtonType {
    OPERATION,
    NUMBER,
    UNIT,
    COMMAND,
    EQUAL
}

enum class SwipeDirection {
    LEFT,
    RIGHT
}

@Composable
fun ResponsiveCalculatorButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    buttonType: ButtonType = ButtonType.NUMBER,
    fontSize: androidx.compose.ui.unit.TextUnit = 20.sp,
    isSelected: Boolean = false
) {
    val buttonColor = when {
        isSelected && buttonType == ButtonType.OPERATION -> OperationSelectedColor
        buttonType == ButtonType.OPERATION -> OperationColor
        buttonType == ButtonType.NUMBER -> NumberColor
        buttonType == ButtonType.UNIT -> UnitColor
        buttonType == ButtonType.COMMAND -> CommandColor
        buttonType == ButtonType.EQUAL -> EqualColor
        else -> NumberColor
    }
    
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxHeight(),
        shape = RectangleShape,
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = buttonColor
        )
    ) {
        Text(
            text = text,
            fontSize = fontSize,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            maxLines = 1,
            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun CalculatorButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    buttonType: ButtonType = ButtonType.NUMBER,
    isSelected: Boolean = false
) {
    ResponsiveCalculatorButton(
        text = text,
        onClick = onClick,
        modifier = modifier,
        buttonType = buttonType,
        isSelected = isSelected
    )
}

@Preview(showBackground = true)
@Composable
fun CalculatorPreview() {
    DistributionCalculatorTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Calculator()
        }
    }
}
