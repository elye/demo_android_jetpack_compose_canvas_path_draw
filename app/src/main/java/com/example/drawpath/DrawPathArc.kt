package com.example.drawpath

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp

@Composable
fun DrawPathArc() {
    val configuration = LocalConfiguration.current
    val width = configuration.screenWidthDp.dp - 32.dp
    var x1Position by remember { mutableStateOf(0f) }
    var y1Position by remember { mutableStateOf(0f) }
    var x2Position by remember { mutableStateOf(0f) }
    var y2Position by remember { mutableStateOf(0f) }
    var startAngle by remember { mutableStateOf(0f) }
    var sweepAngle by remember { mutableStateOf(0f) }
    val height = 200.dp

    Column(modifier = Modifier.padding(16.dp)) {

        Spacer(modifier = Modifier.height(16.dp))
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .background(Color.Yellow)
        ) {
            drawPath(
                Path().apply {
                arcTo(
                    Rect(
                        x1Position.dp.toPx(), y1Position.dp.toPx(),
                        x2Position.dp.toPx(), y2Position.dp.toPx()
                    ),
                    startAngle, sweepAngle, true
                )
            }, Color.Black,
                style = Stroke(2.dp.value)
            )

            drawPoints(
                listOf(
                    Offset(x1Position.dp.toPx(), y1Position.dp.toPx()),
                    Offset(x2Position.dp.toPx(), y2Position.dp.toPx())
                ),
                PointMode.Points,
                Color.Red,
                strokeWidth = 8.dp.toPx(),
                cap = StrokeCap.Round
            )
        }
        Text(text = "X1: $x1Position")
        Slider(value = x1Position,
            valueRange = 0f..width.value,
            onValueChange = {
                x1Position = it
            }
        )
        Text(text = "Y1: $y1Position")
        Slider(value = y1Position,
            valueRange = 0f..height.value,
            onValueChange = {
                y1Position = it
            }
        )
        Text(text = "X2: $x2Position")
        Slider(value = x2Position,
            valueRange = 0f..width.value,
            onValueChange = {
                x2Position = it
            }
        )
        Text(text = "Y2: $y2Position")
        Slider(value = y2Position,
            valueRange = 0f..height.value,
            onValueChange = {
                y2Position = it
            }
        )
        Text(text = "Start Angle: $startAngle")
        Slider(value = startAngle,
            valueRange = 0f..360f,
            onValueChange = {
                startAngle = it
            }
        )
        Text(text = "Sweep Angle: $sweepAngle")
        Slider(value = sweepAngle,
            valueRange = 0f..360f,
            onValueChange = {
                sweepAngle = it
            }
        )
    }
}
