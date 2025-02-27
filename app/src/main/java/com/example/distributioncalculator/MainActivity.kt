package com.example.distributioncalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.input.KeyboardType
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
                    DistributionCalculator(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun DistributionCalculator(modifier: Modifier = Modifier) {
    // Input states for first distribution
    var mean1 by remember { mutableStateOf("0") }
    var stdDev1 by remember { mutableStateOf("1") }
    
    // Input states for second distribution
    var mean2 by remember { mutableStateOf("0") }
    var stdDev2 by remember { mutableStateOf("1") }
    
    // Output states
    var resultMean by remember { mutableStateOf("0") }
    var resultStdDev by remember { mutableStateOf("0") }
    
    // Current operation
    var operation by remember { mutableStateOf("+") }
    
    // Current input field focus
    var currentInputField by remember { mutableStateOf("mean1") }

    fun calculateDistribution() {
        // This is a stub for the actual distribution calculation logic
        // In a real implementation, this would perform statistical operations on distributions
        
        val m1 = mean1.toDoubleOrNull() ?: 0.0
        val sd1 = stdDev1.toDoubleOrNull() ?: 1.0
        val m2 = mean2.toDoubleOrNull() ?: 0.0
        val sd2 = stdDev2.toDoubleOrNull() ?: 1.0
        
        // Simple placeholder calculations
        when (operation) {
            "+" -> {
                // For addition of independent random variables, means add and variances add
                resultMean = (m1 + m2).toString()
                resultStdDev = Math.sqrt(sd1 * sd1 + sd2 * sd2).toString()
            }
            "-" -> {
                // For subtraction of independent random variables
                resultMean = (m1 - m2).toString()
                resultStdDev = Math.sqrt(sd1 * sd1 + sd2 * sd2).toString()
            }
            "×" -> {
                // This is a placeholder - multiplication of distributions is more complex
                resultMean = (m1 * m2).toString()
                resultStdDev = (sd1 * sd2).toString()
            }
            "÷" -> {
                // This is a placeholder - division of distributions is more complex
                if (m2 != 0.0) {
                    resultMean = (m1 / m2).toString()
                    resultStdDev = (sd1 / sd2).toString()
                } else {
                    resultMean = "Error"
                    resultStdDev = "Error"
                }
            }
        }
    }
    
    fun onNumberClick(number: Int) {
        when (currentInputField) {
            "mean1" -> mean1 = if (mean1 == "0") number.toString() else mean1 + number.toString()
            "stdDev1" -> stdDev1 = if (stdDev1 == "0") number.toString() else stdDev1 + number.toString()
            "mean2" -> mean2 = if (mean2 == "0") number.toString() else mean2 + number.toString()
            "stdDev2" -> stdDev2 = if (stdDev2 == "0") number.toString() else stdDev2 + number.toString()
        }
    }
    
    fun onClearClick() {
        when (currentInputField) {
            "mean1" -> mean1 = "0"
            "stdDev1" -> stdDev1 = "0"
            "mean2" -> mean2 = "0"
            "stdDev2" -> stdDev2 = "0"
        }
    }
    
    fun onDecimalClick() {
        when (currentInputField) {
            "mean1" -> if (!mean1.contains(".")) mean1 = "$mean1."
            "stdDev1" -> if (!stdDev1.contains(".")) stdDev1 = "$stdDev1."
            "mean2" -> if (!mean2.contains(".")) mean2 = "$mean2."
            "stdDev2" -> if (!stdDev2.contains(".")) stdDev2 = "$stdDev2."
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title
        Text(
            text = "Distribution Calculator",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        
        // First Distribution Input
        DistributionInput(
            title = "Distribution 1",
            mean = mean1,
            stdDev = stdDev1,
            onMeanChange = { mean1 = it },
            onStdDevChange = { stdDev1 = it },
            onMeanFocus = { currentInputField = "mean1" },
            onStdDevFocus = { currentInputField = "stdDev1" },
            currentField = currentInputField
        )
        
        // Operation Selection
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            OperationButton(text = "+", isSelected = operation == "+") { operation = "+"; calculateDistribution() }
            OperationButton(text = "-", isSelected = operation == "-") { operation = "-"; calculateDistribution() }
            OperationButton(text = "×", isSelected = operation == "×") { operation = "×"; calculateDistribution() }
            OperationButton(text = "÷", isSelected = operation == "÷") { operation = "÷"; calculateDistribution() }
        }
        
        // Second Distribution Input
        DistributionInput(
            title = "Distribution 2",
            mean = mean2,
            stdDev = stdDev2,
            onMeanChange = { mean2 = it },
            onStdDevChange = { stdDev2 = it },
            onMeanFocus = { currentInputField = "mean2" },
            onStdDevFocus = { currentInputField = "stdDev2" },
            currentField = currentInputField
        )
        
        // Number Pad
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // First row - 7, 8, 9
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

            // Second row - 4, 5, 6
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

            // Third row - 1, 2, 3
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

            // Fourth row - 0, ., C
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CalculatorButton(
                    text = "0",
                    onClick = { onNumberClick(0) },
                    modifier = Modifier.weight(1f)
                )
                CalculatorButton(
                    text = ".",
                    onClick = { onDecimalClick() },
                    modifier = Modifier.weight(1f)
                )
                CalculatorButton(
                    text = "C",
                    onClick = { onClearClick() },
                    modifier = Modifier.weight(1f)
                )
            }
        }
        
        // Calculate Button
        Button(
            onClick = { calculateDistribution() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Calculate",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
        
        // Result Display
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Result Distribution",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Mean: $resultMean")
                    Text(text = "Std Dev: $resultStdDev")
                }
            }
        }
    }
}

@Composable
fun DistributionInput(
    title: String,
    mean: String,
    stdDev: String,
    onMeanChange: (String) -> Unit,
    onStdDevChange: (String) -> Unit,
    onMeanFocus: () -> Unit,
    onStdDevFocus: () -> Unit,
    currentField: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedTextField(
                    value = mean,
                    onValueChange = onMeanChange,
                    label = { Text("Mean") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 4.dp)
                        .clickable { onMeanFocus() }
                )
                
                OutlinedTextField(
                    value = stdDev,
                    onValueChange = onStdDevChange,
                    label = { Text("Std Dev") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 4.dp)
                        .clickable { onStdDevFocus() }
                )
            }
        }
    }
}

@Composable
fun OperationButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .padding(4.dp)
            .height(48.dp)
            .width(48.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = text,
            fontSize = 20.sp,
            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold
        )
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
            .height(56.dp)
    ) {
        Text(
            text = text,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DistributionCalculatorPreview() {
    DistributionCalculatorTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            DistributionCalculator()
        }
    }
}