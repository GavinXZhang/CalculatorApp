package com.example.calculatorapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calculatorapp.ui.theme.CalculatorAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CalculatorAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    CalculatorApp(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}
@Composable
fun CalculatorApp(modifier: Modifier = Modifier) {
    var displayText by remember { mutableStateOf("0") }
    val buttonSpacing = 8.dp

    // Function to evaluate expressions
    fun evaluateExpression(expression: String): Double {
        return try {
            val result = ExpressionEvaluator().evaluate(expression)
            result
        } catch (e: Exception) {
            Double.NaN // Return NaN for invalid expressions
        }
    }

    // Function to handle button clicks
    fun onButtonClick(buttonValue: String) {
        when (buttonValue) {
            "C" -> {
                // Reset everything on "Clear All"
                displayText = "0"
            }
            "=" -> {
                // Evaluate the entire expression when "=" is pressed
                val result = evaluateExpression(displayText)
                displayText = result.toString()
            }
            "^2" -> {
                // Append "^2" and calculate immediately for squaring
                val result = evaluateExpression("$displayText^2")
                displayText = result.toString()
            }
            else -> {
                // Handle typing numbers or operators
                if (displayText == "0") {
                    displayText = buttonValue
                } else {
                    displayText += buttonValue
                }
            }
        }
    }

    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Display field
        BasicTextField(
            value = displayText,
            onValueChange = { newText ->
                displayText = newText // Update the display with typed input
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            textStyle = LocalTextStyle.current.copy(
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold
            )
        )

        // Buttons
        Column(
            verticalArrangement = Arrangement.spacedBy(buttonSpacing),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CalculatorRow(listOf("7", "8", "9", "/"), onClick = { onButtonClick(it) })
            CalculatorRow(listOf("4", "5", "6", "*"), onClick = { onButtonClick(it) })
            CalculatorRow(listOf("1", "2", "3", "-"), onClick = { onButtonClick(it) })
            CalculatorRow(listOf("0", ".", "=", "+"), onClick = { onButtonClick(it) })
            CalculatorRow(listOf("^2", "C"), onClick = { onButtonClick(it) }) // Square and Clear All buttons
        }
    }
}

@Composable
fun CalculatorRow(buttons: List<String>, onClick: (String) -> Unit) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        buttons.forEach { label ->
            Button(
                onClick = { onClick(label) },
                modifier = Modifier.weight(1f)
            ) {
                Text(text = label, fontSize = 24.sp)
            }
        }
    }
}

// Modify the ExpressionEvaluator to handle squaring (^2)
class ExpressionEvaluator {
    fun evaluate(expression: String): Double {
        // Handle square (x^2) operations separately
        if (expression.contains("^2")) {
            val number = expression.removeSuffix("^2").toDoubleOrNull()
            return number?.let { it * it } ?: Double.NaN
        }

        // Handle normal operations
        val tokens = expression.split("(?<=[-+*/])|(?=[-+*/])".toRegex()).map { it.trim() }
        if (tokens.size < 3) return Double.NaN

        var result = tokens[0].toDoubleOrNull() ?: return Double.NaN
        var operator = ""

        for (i in 1 until tokens.size) {
            when {
                tokens[i] == "+" || tokens[i] == "-" || tokens[i] == "*" || tokens[i] == "/" -> operator = tokens[i]
                operator.isNotEmpty() -> {
                    val nextOperand = tokens[i].toDoubleOrNull() ?: return Double.NaN
                    result = when (operator) {
                        "+" -> result + nextOperand
                        "-" -> result - nextOperand
                        "*" -> result * nextOperand
                        "/" -> if (nextOperand != 0.0) result / nextOperand else return Double.NaN
                        else -> return Double.NaN
                    }
                    operator = ""
                }
            }
        }
        return result
    }
}

@Preview(showBackground = true)
@Composable
fun CalculatorAppPreview() {
    CalculatorAppTheme {
        CalculatorApp()
    }
}