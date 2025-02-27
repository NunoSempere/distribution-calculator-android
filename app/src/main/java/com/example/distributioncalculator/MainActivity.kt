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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.distributioncalculator.ui.theme.DistributionCalculatorTheme
import kotlin.math.abs

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
    var selected_input by remember { mutableStateOf(0) }
    var operation by remember { mutableStateOf<String?>(null) }
    // var previousInput by remember { mutableStateOf<String?>(null) }
    var clearOnNextDigit by remember { mutableStateOf(false) }

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
        if (selected_input == 0) {
            input1 = input1 * 10 + number
        } else {
            input2 = input2 * 10 + number
        }
        /*
        input = if (input == 0 || clearOnNextDigit) {
            clearOnNextDigit = false
            // number.toString()
            number
        } else {
            10 * input + number
            // input + number.toString()
        }
        */
    }

    fun onOperationClick(op: String) {
        if (operation != null && !clearOnNextDigit) {
            // val result = calculateResult()
            // output1 = result.first
            // output2 = result.second
            operation = op
        }
        operation = op
        // input1 = 0.0
        // input2 = 0.0
        // clearOnNextDigit = true
    }

    fun onMultiplierClick(multiplier: String) {
			  if (selected_input == 0) {
					input1 = when (multiplier) {
							"K" -> input1 * 1000.0
							"M" -> input1 * 1000.0 * 1000.0
							"B" -> input1 * 1000.0 * 1000.0 * 1000.0
							"T" -> input1 * 1000.0 * 1000.0 * 1000.0 * 1000.0
							else -> input1
					}
					 
				} else {
					input2 = when (multiplier) {
							"K" -> input2 * 1000.0
							"M" -> input2 * 1000.0 * 1000.0
							"B" -> input2 * 1000.0 * 1000.0 * 1000.0
							"T" -> input2 * 1000.0 * 1000.0 * 1000.0 * 1000.0
							else ->  input2
					}
				}

    }


    fun onEqualsClick() {
        if (operation != null) {
            val result = calculateResult()
            output1 = result.first
            output2 = result.second
            operation = null
            input1 = 0.0
            input2 = 0.0
        }
    }

    fun onRestartClick() {
        output1 = 1.0
        output2 = 1.0
        input1 = 0.0
        input2 = 0.0
        operation = null
        clearOnNextDigit = false
    }
    fun onClearClick() {
			  if (selected_input == 0) {
					input1 = 0.0
				} else {
					input2 = 0.0
				}
        clearOnNextDigit = false
    }

    fun onDecimalClick() {
        // TO DO: fix later
        /*
        if (clearOnNextDigit) {
            input = "0."
            clearOnNextDigit = false
        } else if (!input.contains(".")) {
            input = "$input."
        }
        */
    }
    fun onSwitchClick() {
        selected_input = (selected_input + 1) % 2
        // TO DO: fix later
        /*
        if (clearOnNextDigit) {
            input = "0."
            clearOnNextDigit = false
        } else if (!input.contains(".")) {
            input = "$input."
        }
        */
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
            // Output boxes with the same theme as inputs
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .height(100.dp)
                        .weight(1f)
                        .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(16.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Text(
                        text = toPrettyString(output1),
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
                        .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(16.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Text(
                        text = toPrettyString(output2),
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.End,
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

					  // Some special ops
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CalculatorButton(
                    text = "R",
                    onClick = { onRestartClick() },
                    modifier = Modifier.weight(1f)
                )
                CalculatorButton(
                    text = "C",
                    onClick = { onClearClick() },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.weight(1f))
            }

						// Row bis
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
            }


            // Fifth row - 0, .
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CalculatorButton(
                    text = ".",
                    onClick = { onDecimalClick() },
                    modifier = Modifier.weight(1f)
                )
                CalculatorButton(
                    text = "0",
                    onClick = { onNumberClick(0) },
                    modifier = Modifier.weight(1f)
                )
                CalculatorButton(
                    text = "=",
                    onClick = { onEqualsClick() },
                    modifier = Modifier.weight(1f)
                )
            }

						// Multiplier click
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CalculatorButton(
                    text = "K",
                    onClick = { onMultiplierClick("K") },
                    modifier = Modifier.weight(1f)
                )
                CalculatorButton(
                    text = "M",
                    onClick = { onMultiplierClick("M") },
                    modifier = Modifier.weight(1f)
                )
                CalculatorButton(
                    text = "B",
                    onClick = { onMultiplierClick("B") },
                    modifier = Modifier.weight(1f)
                )
                CalculatorButton(
                    text = "T",
                    onClick = { onMultiplierClick("T") },
                    modifier = Modifier.weight(1f)
                )
            }

            // First row - Clear and operations
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CalculatorButton(
                    text = "+",
                    onClick = { onOperationClick("+") },
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
                        .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                        .border(
                            width = if (selected_input == 0) 3.dp else 1.dp,
                            color = if (selected_input == 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable { selected_input = 0 }
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
                        .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                        .border(
                            width = if (selected_input == 1) 3.dp else 1.dp,
                            color = if (selected_input == 1) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable { selected_input = 1 }
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
