package com.nunosempere.distributioncalculator

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.material3.Icon

@Composable
fun Calculator(
    modifier: Modifier = Modifier,
    history: String = "",
    onHistoryUpdate: (String) -> Unit = {},
    cs_shared: CalculatorState,
    onCsSharedUpdate: (CalculatorState) -> Unit = {},
    onNavigateToTips: () -> Unit = {},
    onNavigateToHistory: () -> Unit = {}
) {
    var cs by remember { mutableStateOf(cs_shared) }

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
        val input = Distribution.Lognormal(low = cs.inputFieldLow, high = cs.inputFieldHigh)
        val result = when (cs.operation) {
            "×" -> MultiplyDists(input, cs.output) 
            "÷" -> DivideDists(cs.output, input)
            "+" -> SumDists(input, cs.output)
            "-" -> SubstractDists(cs.output, input)
            else -> Distribution.Err("Unsupported operation type")
        }
        onHistoryUpdate(history + "\n" + cs.operation + " " + cs.inputFieldLow + " " + cs.inputFieldHigh)
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
        if(cs.selectedInput == 0){
            if(cs.onDecimalInput == 0){
                cs = cs.copy(inputFieldLow = cs.inputFieldLow * 10 + number)
            } else {
                cs = cs.copy(
                    inputFieldLow = cs.inputFieldLow + number * 10.0.pow(cs.onDecimalLevel),
                    onDecimalLevel = cs.onDecimalLevel - 1
                )
            }
            cs = cs.copy(inputFieldHigh = max(cs.inputFieldLow, cs.inputFieldHigh))
        } else {
            if(cs.onDecimalInput == 0){
                cs = cs.copy(inputFieldHigh = cs.inputFieldHigh * 10 + number)
            } else {
                cs = cs.copy(
                    inputFieldHigh = cs.inputFieldHigh + number * 10.0.pow(cs.onDecimalLevel),
                    onDecimalLevel = cs.onDecimalLevel - 1
                )
            }
        }
    }

    fun onOperationClick(op: String) {
        cs = cs.copy(
            operation = op,
            onDecimalInput = 0,
            onDecimalLevel = -1
        )
    }

    fun onMultiplierClick(multiplier: String) {
        if (cs.selectedInput == 0) {
            val newInputFieldLow = when (multiplier) {
                "%" -> cs.inputFieldLow * 0.01
                "K" -> cs.inputFieldLow * 1000.0
                "M" -> cs.inputFieldLow * 1000.0 * 1000.0
                "B" -> cs.inputFieldLow * 1000.0 * 1000.0 * 1000.0
                "T" -> cs.inputFieldLow * 1000.0 * 1000.0 * 1000.0 * 1000.0
                else -> cs.inputFieldLow
            }
            cs = cs.copy(
                inputFieldLow = newInputFieldLow,
                inputFieldHigh = max(newInputFieldLow, cs.inputFieldHigh),
                selectedInput = 1,
                onDecimalInput = 0,
                onDecimalLevel = -1
            )
        } else {
            val newInputFieldHigh = when (multiplier) {
                "%" -> cs.inputFieldHigh * 0.01
                "K" -> cs.inputFieldHigh * 1000.0
                "M" -> cs.inputFieldHigh * 1000.0 * 1000.0
                "B" -> cs.inputFieldHigh * 1000.0 * 1000.0 * 1000.0
                "T" -> cs.inputFieldHigh * 1000.0 * 1000.0 * 1000.0 * 1000.0
                else -> cs.inputFieldHigh
            }
            cs = cs.copy(
                inputFieldHigh = newInputFieldHigh,
                onDecimalInput = 0,
                onDecimalLevel = -1
            )
        }
    }

    fun onEqualsClick() {
        if (cs.inputFieldLow > cs.inputFieldHigh) {
            throwSnackbar("Error: first field must be lower than second")
            return
        } else if (cs.inputFieldLow == 0.0) {
            throwSnackbar("Error: first field can't be zero yet. If this is important to you, tell Nuño")
            return
        } else if (cs.inputFieldHigh == 0.0 ){
            throwSnackbar("Error: second field can't be zero yet. If this is important to you, tell Nuño")
            return
        }
        
        val result = calculateResult()
        when(result) {
            is Distribution.Lognormal -> {
                cs = cs.copy(
                    outputTagLow = result.low,
                    outputTagHigh = result.high,
                    output = result
                )
            }
            is Distribution.SamplesArray -> {
                val xs = (result.samples).copyOf()
                xs.sort()
                cs = cs.copy(
                    outputTagLow = xs[5_000],
                    outputTagHigh = xs[95_000],
                    output = result
                )
            }
            is Distribution.Err -> {
                throwSnackbar(result.msg)
            }
        }

        cs = cs.copy(
            inputFieldLow = 0.0,
            inputFieldHigh = 0.0,
            operation = "×",
            selectedInput = 0,
            onDecimalInput = 0,
            onDecimalLevel = -1
        )
    }

    fun onRestartClick() {
        cs = CalculatorState()
    }

    fun onClearClick() {
        if (cs.selectedInput == 0) {
            cs = cs.copy(inputFieldLow = 0.0)
        } else {
            cs = cs.copy(inputFieldHigh = 0.0)
        }
        cs = cs.copy(
            onDecimalInput = 0,
            onDecimalLevel = -1
        )
    }

    fun onDecimalClick() {
        cs = cs.copy(onDecimalInput = 1)
    }

    fun handleSwipe(direction: SwipeDirection) {
        if (!cs.isSwipeProcessing) {
            cs = cs.copy(isSwipeProcessing = true)
            
            when (direction) {
                SwipeDirection.LEFT -> {
                    cs = cs.copy(inputFieldLow = cs.inputFieldHigh)
                }
                SwipeDirection.RIGHT -> {
                    cs = cs.copy(inputFieldHigh = cs.inputFieldLow)
                }
            }
            
            coroutineScope.launch {
                delay(500) // 500ms debounce time
                cs = cs.copy(isSwipeProcessing = false)
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
                                text = toPrettyString(cs.outputTagLow),
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
                                text = toPrettyString(cs.outputTagHigh),
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
                            isSelected = cs.operation == "×"
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
                            isSelected = cs.operation == "+"
                        )
                        ResponsiveCalculatorButton(
                            text = "÷",
                            onClick = { onOperationClick("÷") },
                            modifier = Modifier.weight(1f),
                            buttonType = ButtonType.OPERATION,
                            fontSize = buttonFontSize,
                            isSelected = cs.operation == "÷"
                        )
                        ResponsiveCalculatorButton(
                            text = "-",
                            onClick = { onOperationClick("-") },
                            modifier = Modifier.weight(1f),
                            buttonType = ButtonType.OPERATION,
                            fontSize = buttonFontSize,
                            isSelected = cs.operation == "-"
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
                                onClick = { cs = cs.copy(showMoreOptionsMenu = true) },
                                modifier = Modifier.fillMaxWidth().fillMaxHeight(),
                                buttonType = ButtonType.COMMAND,
                                fontSize = buttonFontSize
                            )
                            DropdownMenu(
                                expanded = cs.showMoreOptionsMenu,
                                onDismissRequest = { cs = cs.copy(showMoreOptionsMenu = false) },
                                modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Tips") },
                                    onClick = {
                                        cs = cs.copy(showMoreOptionsMenu = false)
                                        onCsSharedUpdate(cs)
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
                                        cs = cs.copy(showMoreOptionsMenu = false)
                                        onCsSharedUpdate(cs)
                                        onNavigateToHistory()
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
                                        cs = cs.copy(showMoreOptionsMenu = false)
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
                                        cs = cs.copy(showMoreOptionsMenu = false)
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
                                    cs = cs.copy(isSwipeProcessing = false)
                                },
                                onDragCancel = {
                                    cs = cs.copy(isSwipeProcessing = false)
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
                                    if (cs.selectedInput == 0) MaterialTheme.colorScheme.surfaceVariant else SurfaceVariantSelected,
                                    RectangleShape
                                )
                                .clickable { 
                                    cs = cs.copy(
                                        selectedInput = 0,
                                        onDecimalInput = 0,
                                        onDecimalLevel = -1,
                                        inputFieldLow = 0.0
                                    )
                                }
                                .padding(all = basePadding/2)
                                .zIndex(1f),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Text(
                                text = toPrettyString(cs.inputFieldLow),
                                fontSize = largeFontSize,
                                fontWeight = if (cs.selectedInput == 0) FontWeight.Bold else FontWeight.Normal,
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
                            PercentileIndicator(text = "5%", selected_input = (cs.selectedInput == 0))
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
                                    if (cs.selectedInput == 1) MaterialTheme.colorScheme.surfaceVariant else SurfaceVariantSelected,
                                    RectangleShape
                                )
                                .clickable { 
                                    cs = cs.copy(
                                        selectedInput = 1,
                                        onDecimalInput = 0,
                                        onDecimalLevel = -1,
                                        inputFieldHigh = 0.0
                                    )
                                }
                                .padding(all = basePadding/2)
                                .zIndex(1f),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Text(
                                text = toPrettyString(cs.inputFieldHigh),
                                fontSize = largeFontSize,
                                fontWeight = if (cs.selectedInput == 1) FontWeight.Bold else FontWeight.Normal,
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
                            PercentileIndicator(text = "95%", selected_input = (cs.selectedInput == 1))
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
            Calculator(
                cs_shared = CalculatorState(
                    inputFieldLow = 100.0,
                    inputFieldHigh = 200.0,
                    outputTagLow = 500.0,
                    outputTagHigh = 1000.0,
                    operation = "×"
                ),
                onCsSharedUpdate = { /* Preview doesn't need real implementation */ }
            )
        }
    }
}


