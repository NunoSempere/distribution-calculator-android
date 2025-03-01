package com.example.distributioncalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.example.distributioncalculator.ui.theme.CommandColor
import com.example.distributioncalculator.ui.theme.DistributionCalculatorTheme
import com.example.distributioncalculator.ui.theme.EqualColor
import com.example.distributioncalculator.ui.theme.NumberColor
import com.example.distributioncalculator.ui.theme.OperationColor
import com.example.distributioncalculator.ui.theme.UnitColor
import com.example.distributioncalculator.samples.*

import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.ln
import kotlin.math.sqrt
import kotlin.math.min
import kotlin.math.max
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DistributionCalculatorTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Calculator(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun Calculator(modifier: Modifier = Modifier) {
 
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

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val screenHeight = configuration.screenHeightDp
    
    val basePadding = (min(screenWidth, screenHeight) * 0.04f).dp
    val baseSpacing = (min(screenWidth, screenHeight) * 0.02f).dp // Reduced spacing between buttons
    
    val largeFontSize = max(min(screenWidth, screenHeight) * 0.06f, 20f).sp
    val buttonFontSize = max(min(screenWidth, screenHeight) * 0.06f, 18f).sp

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    func throwSnackbar(error_msg: String){
            coroutineScope.launch {
                snackbarHostState.showSnackbar(error_msg)
            }
    }

    fun calculateResult(): Distribution {
        val input = Distribution.Lognormal(low = input_field_low, high = input_field_high)
        val result = when (operation) {
            "×" -> MultiplyDists(input, output) 
            "÷" -> DivideDists(input, output)
            "+" -> SumDists(input, output)
            "-" -> SubstractDists(input, output)
            else -> throw IllegalStateException("Unsupported operation type")
        }
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
        if (on_decimal_input == 0) {
            if (selected_input == 0) {
                input_field_low = input_field_low * 10 + number
            } else {
                input_field_high = input_field_high * 10 + number
            }
        } else {
            if (selected_input == 0) {
                input_field_low = input_field_low + number * 10.0.pow(on_decimal_level)
            } else {
                input_field_high = input_field_high + number * 10.0.pow(on_decimal_level)
            }
            on_decimal_level = on_decimal_level - 1
        }
    }

    fun onOperationClick(op: String) {
        operation = op
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
            throwSnackbar("Error, first field must be lower than second")
            return
        }
        
        val result = calculateResult()
        when(result) {
            is Distribution.Lognormal -> {
                output_tag_low = result.low
                output_tag_high = result.high
            }
            is Distribution.SamplesArray -> {
                val result_copy = (result.samples).copyOf()
                result_copy.sort()
                output_tag_low = result_copy[5_000]
                output_tag_high = result_copy[95_000]
            }
        }
        output = result

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

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val availableHeight = maxHeight
        val availableWidth = maxWidth
        
        val outputBoxHeight = availableHeight * 0.12f
        val inputBoxHeight = availableHeight * 0.12f

        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
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
                                .padding(all = basePadding),
                            contentAlignment = Alignment.CenterEnd
                        ) {
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
                                .padding(all = basePadding),
                            contentAlignment = Alignment.CenterEnd
                        ) {
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
                            fontSize = buttonFontSize
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
                            fontSize = buttonFontSize
                        )
                        ResponsiveCalculatorButton(
                            text = "÷",
                            onClick = { onOperationClick("÷") },
                            modifier = Modifier.weight(1f),
                            buttonType = ButtonType.OPERATION,
                            fontSize = buttonFontSize
                        )
                        ResponsiveCalculatorButton(
                            text = "-",
                            onClick = { onOperationClick("-") },
                            modifier = Modifier.weight(1f),
                            buttonType = ButtonType.OPERATION,
                            fontSize = buttonFontSize
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
                        ResponsiveCalculatorButton(
                            text = "Clear",
                            onClick = { onClearClick() },
                            modifier = Modifier.weight(1f),
                            buttonType = ButtonType.COMMAND,
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
                            text = "K",
                            onClick = { onMultiplierClick("K") },
                            modifier = Modifier.weight(1f),
                            buttonType = ButtonType.UNIT,
                            fontSize = buttonFontSize
                        )
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

                Column(
                    verticalArrangement = Arrangement.spacedBy(baseSpacing)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(baseSpacing)
                    ) {
                        Box(
                            modifier = Modifier
                                .height(height = inputBoxHeight)
                                .weight(1f)
                                .background(MaterialTheme.colorScheme.surfaceVariant, RectangleShape)
                                .border(
                                    width = if (selected_input == 0) 3.dp else 1.dp,
                                    color = if (selected_input == 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                                    shape = RectangleShape
                                )
                                .clickable { selected_input = 0; on_decimal_input = 0; on_decimal_level = -1 }
                                .padding(all = basePadding),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Text(
                                text = toPrettyString(input_field_low),
                                fontSize = largeFontSize,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.End,
                                maxLines = 1
                            )
                        }
                        Box(
                            modifier = Modifier
                                .height(height = inputBoxHeight)
                                .weight(1f)
                                .background(MaterialTheme.colorScheme.surfaceVariant, RectangleShape)
                                .border(
                                    width = if (selected_input == 1) 3.dp else 1.dp,
                                    color = if (selected_input == 1) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                                    shape = RectangleShape
                                )
                                .clickable { selected_input = 1; on_decimal_input = 0; on_decimal_level = -1 }
                                .padding(all = basePadding),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Text(
                                text = toPrettyString(input_field_high),
                                fontSize = largeFontSize,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.End,
                                maxLines = 1
                            )
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

@Composable
fun ResponsiveCalculatorButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    buttonType: ButtonType = ButtonType.NUMBER,
    fontSize: androidx.compose.ui.unit.TextUnit = 20.sp
) {
    val buttonColor = when (buttonType) {
        ButtonType.OPERATION -> OperationColor
        ButtonType.NUMBER -> NumberColor
        ButtonType.UNIT -> UnitColor
        ButtonType.COMMAND -> CommandColor
        ButtonType.EQUAL -> EqualColor
    }
    
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxHeight(),
        shape = RectangleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = buttonColor
        )
    ) {
        Text(
            text = text,
            fontSize = fontSize,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}

@Composable
fun CalculatorButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    buttonType: ButtonType = ButtonType.NUMBER
) {
    ResponsiveCalculatorButton(
        text = text,
        onClick = onClick,
        modifier = modifier,
        buttonType = buttonType
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
