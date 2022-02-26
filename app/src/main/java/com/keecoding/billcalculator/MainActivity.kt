package com.keecoding.billcalculator

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Alignment.Companion.Start
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.keecoding.billcalculator.components.InputField
import com.keecoding.billcalculator.ui.theme.BillCalculatorTheme
import com.keecoding.billcalculator.widgets.RoundIconButton
import java.text.DecimalFormat
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val billState = remember {
                mutableStateOf(0.0)
            }
            MyApp {
                Column {
                    BillCard(billState)
                    BillForm {
                        billState.value = it
                    }
                }
            }
        }
    }
}

@Composable
fun MyApp(content: @Composable () -> Unit) {
    BillCalculatorTheme(
        darkTheme = false,
    ) {
        Surface(
            modifier = Modifier
                .padding(12.dp),
            color = MaterialTheme.colors.background
        ) {
            content()
        }
    }
}

@Composable
fun BillCard(amount: MutableState<Double>) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .clip(shape = RoundedCornerShape(corner = CornerSize(12.dp))),
        color = Color(0xFF65FF85),
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Total as Person")
            Text(text = "$${amount.value}", style = MaterialTheme.typography.h5)
        }
    }
}

@Composable
fun BillForm(
    onValChange: (Double) -> Unit = {}
) {
    val df = DecimalFormat("0.00");

    val currentBillState = remember {
        mutableStateOf("")
    }
    val validState = remember(currentBillState.value) {
        currentBillState.value.trim().isNotBlank()
    }
    val tipState = remember {
        mutableStateOf(0f)
    }
    val splitState = remember {
        mutableStateOf(1)
    }
    val totalBillState = remember {
        mutableStateOf(0.0)
    }
    val focusManager = LocalFocusManager.current
    Surface(
        modifier = Modifier
            .padding(2.dp)
            .padding(top = 12.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(corner = CornerSize(8.dp)),
        border = BorderStroke(1.dp, Color.LightGray)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Start
        ) {
            val tipp = 0
            InputField(
                modifier = Modifier.fillMaxWidth(),
                valueState = currentBillState,
                labelId = "Enter Bill",
                enabled = true,
                isSingleLine = true,
                onAction = KeyboardActions {
                    if (!validState) return@KeyboardActions
                    onValChange(totalBillState.value)
                    focusManager.clearFocus()
                })
            Row(
                modifier = Modifier.padding(3.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                Text(text = "Split", modifier = Modifier.align(alignment = CenterVertically))
                Spacer(modifier = Modifier.width(120.dp))
                Row(
                    modifier = Modifier.padding(horizontal = 3.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    RoundIconButton(
                        imageVector = Icons.Default.Remove,
                        onClick = {
                            if (splitState.value > 1) {
                                splitState.value--
                                onValChange(totalBillState.value)
                            }
                        })
                    Text(
                        text = splitState.value.toString(), modifier = Modifier
                            .align(CenterVertically)
                            .padding(start = 9.dp, end = 9.dp)
                    )
                    RoundIconButton(
                        imageVector = Icons.Default.Add,
                        onClick = {
                            splitState.value++
                            onValChange(totalBillState.value)
                        })
                }
            }
            Row(modifier = Modifier.padding(top = 24.dp)) {
                Text(
                    text = "Tip / Person", modifier = Modifier
                        .align(CenterVertically)
                )
                Spacer(modifier = Modifier.width(200.dp))
                val tip: Float =
                    if (validState) currentBillState.value.toFloat() * tipState.value else 0.0F
                val roundoff = (tip * 100.0).roundToInt() / 100
                Text(text = "$${df.format(roundoff.toDouble() / splitState.value.toDouble())}")
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "${(tipState.value * 100).toInt()}%")
                Spacer(modifier = Modifier.height(16.dp))
                Slider(value = tipState.value, onValueChange = {
                    tipState.value = it
                    onValChange(totalBillState.value)
                    Log.d("ddd", "BillForm: $tipp")
                })
            }
            val currentState = if (validState) currentBillState.value.trim().toFloat() else 0f
            totalBillState.value =
                ((currentState + (currentState * tipState.value)) / splitState.value).toDouble()
            totalBillState.value = ((totalBillState.value * 100.0).roundToInt() / 100).toDouble()
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    BillCalculatorTheme {
        Greeting("Android")
    }
}