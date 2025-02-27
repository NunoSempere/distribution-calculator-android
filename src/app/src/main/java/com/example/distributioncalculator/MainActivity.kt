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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.example.distributioncalculator.ui.theme.CommandColor
import com.example.distributioncalculator.ui.theme.DistributionCalculatorTheme
import com.example.distributioncalculator.ui.theme.EqualColor
import com.example.distributioncalculator.ui.theme.NumberColor
import com.example.distributioncalculator.ui.theme.OperationColor
import com.example.distributioncalculator.ui.theme.UnitColor

import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.ln
import kotlin.math.sqrt

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

    var output1 by remember { mutableStateOf(1.0) }
    var output2 by remember { mutableStateOf(1.0) }
    var input1 by remember { mutableStateOf(0.0) }
    var input2 by remember { mutableStateOf(0.0) }
    var operation by remember { mutableStateOf("×") }
    var selected_input by remember { mutableStateOf(0) }
    var on_decimal_input by remember {mutableStateOf(0)}
    var on_decimal_level by remember {mutableStateOf(-1)}


    /* Algebra */
    fun calculateResult(): Pair<Double, Double> {
        // Fake operation from now.
        return when (operation) {
            "+" -> Pair(output1 + input1, output2 + input2)
            "-" -> Pair(output1 - input1, output2 - input2)
            "×" -> Pair(output1 * input1, output2 * input2)
            "÷" -> {
                if (input2 == 0.0 || input1 == 0.0){
                    Pair(output1, output2)
                    // TODO: "Error"
                } else {
                    Pair(output1 / input1, output2 / input2)
                }
            }
            else -> Pair(output1, output2)
        }
    }

    fun toPrettyString(d: Double): String {
        return when {
            abs(d) >= 1_000_000_000_000.0 -> {
                "%.2fT".format(d / 1_000_000_000_000.0)
            }
            abs(d) >= 1_000_000_000.0 -> {
                "%.2fB".format(d / 1_000_000_000.0)
            }
            abs(d) >= 1_000_000.0 -> {
                "%.2fM".format(d / 1_000_000.0)
            }
            abs(d) >= 1_000.0 -> {
                "%.2fK".format(d / 1_000.0)
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
                input1 = input1 * 10 + number
            } else {
                input2 = input2 * 10 + number
            }
        } else {
            if (selected_input == 0) {
                input1 = input1 + number * 10.0.pow(on_decimal_level)
            } else {
                input2 = input2 + number * 10.0.pow(on_decimal_level)
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
            input1 = when (multiplier) {
                "%" -> input1 * 0.01
                "K" -> input1 * 1000.0
                "M" -> input1 * 1000.0 * 1000.0
                "B" -> input1 * 1000.0 * 1000.0 * 1000.0
                "T" -> input1 * 1000.0 * 1000.0 * 1000.0 * 1000.0
                else -> input1
            }
            selected_input = 1
        } else {
            input2 = when (multiplier) {
                "%" -> input2 * 0.01
                "K" -> input2 * 1000.0
                "M" -> input2 * 1000.0 * 1000.0
                "B" -> input2 * 1000.0 * 1000.0 * 1000.0
                "T" -> input2 * 1000.0 * 1000.0 * 1000.0 * 1000.0
                else -> input2
            }
        }
        on_decimal_input = 0
        on_decimal_level = -1
    }

    fun onEqualsClick() {
        val result = calculateResult()
        output1 = result.first
        output2 = result.second

        input1 = 0.0
        input2 = 0.0
        operation = "×" 
        selected_input = 0
        on_decimal_input = 0
        on_decimal_level = -1
    }

    fun onRestartClick() {
        output1 = 1.0
        output2 = 1.0

        input1 = 0.0
        input2 = 0.0
        operation = "×" 
        selected_input = 0
        on_decimal_input = 0
        on_decimal_level = -1
    }
    fun onClearClick() {
        if (selected_input == 0) {
            input1 = 0.0
        } else {
            input2 = 0.0
        }
        on_decimal_input = 0
        on_decimal_level = -1
    }

    fun onDecimalClick() {
        on_decimal_input = 1
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Display
        
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Output boxes with different style from inputs
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .height(100.dp)
                        .weight(1f)
                        .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(8.dp))
                        .padding(16.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Text(
                        text = toPrettyString(output1),
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.End,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        maxLines = 1
                    )
                }
                Box(
                    modifier = Modifier
                        .height(100.dp)
                        .weight(1f)
                        .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(8.dp))
                        .padding(16.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Text(
                        text = toPrettyString(output2),
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.End,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        maxLines = 1
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Buttons
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            // First row - Multiplication operator alone
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CalculatorButton(
                    text = "×",
                    onClick = { onOperationClick("×") },
                    modifier = Modifier.fillMaxWidth(),
                    buttonType = ButtonType.OPERATION
                )
            }

            // Second row - Other operations
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CalculatorButton(
                    text = "+",
                    onClick = { onOperationClick("+") },
                    modifier = Modifier.weight(1f),
                    buttonType = ButtonType.OPERATION
                )
                CalculatorButton(
                    text = "÷",
                    onClick = { onOperationClick("÷") },
                    modifier = Modifier.weight(1f),
                    buttonType = ButtonType.OPERATION
                )
                CalculatorButton(
                    text = "-",
                    onClick = { onOperationClick("-") },
                    modifier = Modifier.weight(1f),
                    buttonType = ButtonType.OPERATION
                )
            }

			// Row with numbers 1-3
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CalculatorButton(
                    text = "1",
                    onClick = { onNumberClick(1) },
                    modifier = Modifier.weight(1f),
                    buttonType = ButtonType.NUMBER
                )
                CalculatorButton(
                    text = "2",
                    onClick = { onNumberClick(2) },
                    modifier = Modifier.weight(1f),
                    buttonType = ButtonType.NUMBER
                )
                CalculatorButton(
                    text = "3",
                    onClick = { onNumberClick(3) },
                    modifier = Modifier.weight(1f),
                    buttonType = ButtonType.NUMBER
                )
            }

            // Row with numbers 4-6
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CalculatorButton(
                    text = "4",
                    onClick = { onNumberClick(4) },
                    modifier = Modifier.weight(1f),
                    buttonType = ButtonType.NUMBER
                )
                CalculatorButton(
                    text = "5",
                    onClick = { onNumberClick(5) },
                    modifier = Modifier.weight(1f),
                    buttonType = ButtonType.NUMBER
                )
                CalculatorButton(
                    text = "6",
                    onClick = { onNumberClick(6) },
                    modifier = Modifier.weight(1f),
                    buttonType = ButtonType.NUMBER
                )
            }
            
            // Row with numbers 7-9
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CalculatorButton(
                    text = "7",
                    onClick = { onNumberClick(7) },
                    modifier = Modifier.weight(1f),
                    buttonType = ButtonType.NUMBER
                )
                CalculatorButton(
                    text = "8",
                    onClick = { onNumberClick(8) },
                    modifier = Modifier.weight(1f),
                    buttonType = ButtonType.NUMBER
                )
                CalculatorButton(
                    text = "9",
                    onClick = { onNumberClick(9) },
                    modifier = Modifier.weight(1f),
                    buttonType = ButtonType.NUMBER
                )
            }

            // Row with decimal and 0
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CalculatorButton(
                    text = "Restart",
                    onClick = { onRestartClick() },
                    modifier = Modifier.weight(1f),
                    buttonType = ButtonType.COMMAND
                )
                CalculatorButton(
                    text = "0",
                    onClick = { onNumberClick(0) },
                    modifier = Modifier.weight(1f),
                    buttonType = ButtonType.NUMBER
                )
                CalculatorButton(
                    text = ".",
                    onClick = { onDecimalClick() },
                    modifier = Modifier.weight(1f),
                    buttonType = ButtonType.NUMBER
                )
            }

			// Special ops row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CalculatorButton(
                    text = "Clear",
                    onClick = { onClearClick() },
                    modifier = Modifier.weight(1f),
                    buttonType = ButtonType.COMMAND
                )
                CalculatorButton(
                    text = "%",
                    onClick = { onMultiplierClick("%") },
                    modifier = Modifier.weight(1f),
                    buttonType = ButtonType.UNIT
                )
            }

			// Multiplier row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CalculatorButton(
                    text = "K",
                    onClick = { onMultiplierClick("K") },
                    modifier = Modifier.weight(1f),
                    buttonType = ButtonType.UNIT
                )
                CalculatorButton(
                    text = "M",
                    onClick = { onMultiplierClick("M") },
                    modifier = Modifier.weight(1f),
                    buttonType = ButtonType.UNIT
                )
                CalculatorButton(
                    text = "B",
                    onClick = { onMultiplierClick("B") },
                    modifier = Modifier.weight(1f),
                    buttonType = ButtonType.UNIT
                )
                CalculatorButton(
                    text = "T",
                    onClick = { onMultiplierClick("T") },
                    modifier = Modifier.weight(1f),
                    buttonType = ButtonType.UNIT
                )
            }

            
            // Last row - Equals operator alone
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CalculatorButton(
                    text = "=",
                    onClick = { onEqualsClick() },
                    modifier = Modifier.fillMaxWidth(),
                    buttonType = ButtonType.EQUAL
                )
            }
        }

        // Inputs
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Input boxes with decorators to show which is selected
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .height(100.dp)
                        .weight(1f)
                        .background(MaterialTheme.colorScheme.surfaceVariant, RectangleShape)
                        .border(
                            width = if (selected_input == 0) 3.dp else 1.dp,
                            color = if (selected_input == 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                            shape = RectangleShape
                        )
                        .clickable { selected_input = 0; on_decimal_input = 0; on_decimal_level = -1 }
                        .padding(16.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Text(
                        text = toPrettyString(input1),
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.End,
                        maxLines = 1
                    )
                }
                Box(
                    modifier = Modifier
                        .height(100.dp)
                        .weight(1f)
                        .background(MaterialTheme.colorScheme.surfaceVariant, RectangleShape)
                        .border(
                            width = if (selected_input == 1) 3.dp else 1.dp,
                            color = if (selected_input == 1) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                            shape = RectangleShape
                        )
                        .clickable { selected_input = 1; on_decimal_input = 0; on_decimal_level = -1 }
                        .padding(16.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Text(
                        text = toPrettyString(input2),
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.End,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

// Enum to categorize button types
enum class ButtonType {
    OPERATION,
    NUMBER,
    UNIT,
    COMMAND,
    EQUAL
}

@Composable
fun CalculatorButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    buttonType: ButtonType = ButtonType.NUMBER
) {
    // Choose color based on button type
    val buttonColor = when (buttonType) {
        ButtonType.OPERATION -> OperationColor
        ButtonType.NUMBER -> NumberColor
        ButtonType.UNIT -> UnitColor
        ButtonType.COMMAND -> CommandColor
        ButtonType.EQUAL -> EqualColor
    }
    
    Button(
        onClick = onClick,
        modifier = modifier
            .height(64.dp),
        shape = RectangleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = buttonColor
        )
    ) {
        Text(
            text = text,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
    }
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
