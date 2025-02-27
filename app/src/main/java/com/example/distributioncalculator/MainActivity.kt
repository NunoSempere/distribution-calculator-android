package com.example.distributioncalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.distributioncalculator.ui.theme.DistributionCalculatorTheme

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
    var input by remember { mutableStateOf("0") }
    var operation by remember { mutableStateOf<String?>(null) }
    var previousInput by remember { mutableStateOf<String?>(null) }
    var clearOnNextDigit by remember { mutableStateOf(false) }

    fun calculateResult(): String {
        val firstNumber = previousInput?.toDoubleOrNull() ?: 0.0
        val secondNumber = input.toDoubleOrNull() ?: 0.0

        return when (operation) {
            "+" -> (firstNumber + secondNumber).toString()
            "-" -> (firstNumber - secondNumber).toString()
            "×" -> (firstNumber * secondNumber).toString()
            "÷" -> {
                if (secondNumber == 0.0) {
                    "Error"
                } else {
                    (firstNumber / secondNumber).toString()
                }
            }
            else -> input
        }
    }

    fun onNumberClick(number: Int) {
        input = if (input == "0" || clearOnNextDigit) {
            clearOnNextDigit = false
            number.toString()
        } else {
            input + number.toString()
        }
    }

    fun onOperationClick(op: String) {
        if (operation != null && !clearOnNextDigit) {
            input = calculateResult()
        }
        operation = op
        previousInput = input
        clearOnNextDigit = true
    }

    fun onEqualsClick() {
        if (operation != null) {
            input = calculateResult()
            operation = null
            previousInput = null
        }
    }

    fun onClearClick() {
        input = "0"
        operation = null
        previousInput = null
        clearOnNextDigit = false
    }

    fun onDecimalClick() {
        if (clearOnNextDigit) {
            input = "0."
            clearOnNextDigit = false
        } else if (!input.contains(".")) {
            input = "$input."
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Display
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                .padding(16.dp),
            contentAlignment = Alignment.CenterEnd
        ) {
            Text(
                text = input,
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.End,
                maxLines = 1
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Buttons
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // First row - Clear and operations
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CalculatorButton(
                    text = "C",
                    onClick = { onClearClick() },
                    modifier = Modifier.weight(1f)
                )
                CalculatorButton(
                    text = "÷",
                    onClick = { onOperationClick("÷") },
                    modifier = Modifier.weight(1f)
                )
                CalculatorButton(
                    text = "×",
                    onClick = { onOperationClick("×") },
                    modifier = Modifier.weight(1f)
                )
                CalculatorButton(
                    text = "-",
                    onClick = { onOperationClick("-") },
                    modifier = Modifier.weight(1f)
                )
            }

            // Second row - 7, 8, 9, +
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CalculatorButton(
                    text = "7",
                    onClick = { onNumberClick(7) },
                    modifier = Modifier.weight(1f)
                )
                CalculatorButton(
                    text = "8",
                    onClick = { onNumberClick(8) },
                    modifier = Modifier.weight(1f)
                )
                CalculatorButton(
                    text = "9",
                    onClick = { onNumberClick(9) },
                    modifier = Modifier.weight(1f)
                )
                CalculatorButton(
                    text = "+",
                    onClick = { onOperationClick("+") },
                    modifier = Modifier.weight(1f)
                )
            }

            // Third row - 4, 5, 6
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CalculatorButton(
                    text = "4",
                    onClick = { onNumberClick(4) },
                    modifier = Modifier.weight(1f)
                )
                CalculatorButton(
                    text = "5",
                    onClick = { onNumberClick(5) },
                    modifier = Modifier.weight(1f)
                )
                CalculatorButton(
                    text = "6",
                    onClick = { onNumberClick(6) },
                    modifier = Modifier.weight(1f)
                )
                CalculatorButton(
                    text = "=",
                    onClick = { onEqualsClick() },
                    modifier = Modifier.weight(1f)
                )
            }

            // Fourth row - 1, 2, 3, =
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CalculatorButton(
                    text = "1",
                    onClick = { onNumberClick(1) },
                    modifier = Modifier.weight(1f)
                )
                CalculatorButton(
                    text = "2",
                    onClick = { onNumberClick(2) },
                    modifier = Modifier.weight(1f)
                )
                CalculatorButton(
                    text = "3",
                    onClick = { onNumberClick(3) },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.weight(1f))
            }

            // Fifth row - 0, .
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CalculatorButton(
                    text = "0",
                    onClick = { onNumberClick(0) },
                    modifier = Modifier.weight(2f)
                )
                CalculatorButton(
                    text = ".",
                    onClick = { onDecimalClick() },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun CalculatorButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .height(64.dp)
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