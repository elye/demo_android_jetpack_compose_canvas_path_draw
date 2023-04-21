package com.example.drawpath

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import kotlin.math.roundToInt

@Composable
fun DrawPathQuadExplain() {
    val configuration = LocalConfiguration.current
    val width = configuration.screenWidthDp.dp - 32.dp
    val height = 200.dp

    var x0Position by remember { mutableStateOf(0f) }
    var y0Position by remember { mutableStateOf(height.value) }
    var x1Position by remember { mutableStateOf(width.value/2) }
    var y1Position by remember { mutableStateOf(0f) }
    var x2Position by remember(width) { mutableStateOf(width.value) }
    var y2Position by remember(height) { mutableStateOf(height.value) }

    var position by remember { mutableStateOf(0f) }

    Column(modifier = Modifier.padding(16.dp)) {

        Spacer(modifier = Modifier.height(16.dp))
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .background(Color.Yellow)
        ) {
            drawLine(Color.Gray,
                Offset(x0Position.dp.toPx(), y0Position.dp.toPx()),
                Offset(x1Position.dp.toPx(), y1Position.dp.toPx())
            )
            drawLine(Color.Gray,
                Offset(x1Position.dp.toPx(), y1Position.dp.toPx()),
                Offset(x2Position.dp.toPx(), y2Position.dp.toPx())
            )

            val lerpPointX1 = lerp(x0Position.dp, x1Position.dp, position)
            val lerpPointY1 = lerp(y0Position.dp, y1Position.dp, position)
            val lerpPointX2 = lerp(x1Position.dp, x2Position.dp, position)
            val lerpPointY2 = lerp(y1Position.dp, y2Position.dp, position)

            val lerpQuadPointX = lerp(lerpPointX1, lerpPointX2, position)
            val lerpQuadPointY = lerp(lerpPointY1, lerpPointY2, position)
            drawLine(Color.Red,
                Offset(lerpPointX1.toPx(), lerpPointY1.toPx()),
                Offset(lerpPointX2.toPx(), lerpPointY2.toPx()))

            drawPath(
                Path().apply {
                    moveTo(x0Position.dp.toPx(), y0Position.dp.toPx())
                    quadraticBezierTo(
                        lerpPointX1.toPx(), lerpPointY1.toPx(),
                        lerpQuadPointX.toPx(), lerpQuadPointY.toPx(),
                    )
                }, Color.Black,
                style = Stroke(2.dp.value)
            )

            drawPoints(
                listOf(
                    Offset(x0Position.dp.toPx(), y0Position.dp.toPx()),
                    Offset(x1Position.dp.toPx(), y1Position.dp.toPx()),
                    Offset(x2Position.dp.toPx(), y2Position.dp.toPx()),
                ),
                PointMode.Points,
                Color.Gray,
                strokeWidth = 8.dp.toPx(),
                cap = StrokeCap.Round
            )

            drawPoints(
                listOf(
                    Offset(lerpPointX1.toPx(), lerpPointY1.toPx()),
                    Offset(lerpPointX2.toPx(), lerpPointY2.toPx())
                ),
                PointMode.Points,
                Color.Red,
                strokeWidth = 8.dp.toPx(),
                cap = StrokeCap.Round
            )

            drawPoints(
                listOf(
                    Offset(lerpQuadPointX.toPx(), lerpQuadPointY.toPx())
                ),
                PointMode.Points,
                Color.Black,
                strokeWidth = 12.dp.toPx(),
                cap = StrokeCap.Round
            )
        }

        Text(text = "Position: $position")
        Slider(value = position,
            valueRange = 0f..1f,
            onValueChange = {
                position = it
            }
        )

        Row {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "X0: ${x0Position.roundToInt()}")
                Slider(value = x0Position,
                    valueRange = 0f..width.value,
                    onValueChange = {
                        x0Position = it
                    }
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Y0: ${y0Position.roundToInt()}")
                Slider(value = y0Position,
                    valueRange = 0f..height.value,
                    onValueChange = {
                        y0Position = it
                    }
                )
            }
        }

        Row {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "X1: ${x1Position.roundToInt()}")
                Slider(value = x1Position,
                    valueRange = 0f..width.value,
                    onValueChange = {
                        x1Position = it
                    }
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Y1: ${y1Position.roundToInt()}")
                Slider(value = y1Position,
                    valueRange = 0f..height.value,
                    onValueChange = {
                        y1Position = it
                    }
                )
            }
        }
        Row {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "X2: ${x2Position.roundToInt()}")
                Slider(value = x2Position,
                    valueRange = 0f..width.value,
                    onValueChange = {
                        x2Position = it
                    }
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Y2: ${y2Position.roundToInt()}")
                Slider(value = y2Position,
                    valueRange = 0f..height.value,
                    onValueChange = {
                        y2Position = it
                    }
                )
            }
        }
    }
}
