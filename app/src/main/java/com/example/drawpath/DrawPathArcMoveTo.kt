package com.example.drawpath

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Slider
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@Composable
fun DrawPathArcMoveTo() {
    val configuration = LocalConfiguration.current
    val width = configuration.screenWidthDp.dp - 32.dp
    val height = 200.dp
    var startAngle1 by remember { mutableStateOf(0f) }
    var sweepAngle1 by remember { mutableStateOf(0f) }
    var moveTo1 by remember { mutableStateOf(false) }
    var point11 by remember { mutableStateOf(Offset(0f, 0f)) }
    var point12 by remember { mutableStateOf(Offset(0f, 0f)) }
    var startAngle2 by remember { mutableStateOf(0f) }
    var sweepAngle2 by remember { mutableStateOf(0f) }
    var moveTo2 by remember { mutableStateOf(false) }
    var point21 by remember { mutableStateOf(Offset(0f, 0f)) }
    var point22 by remember { mutableStateOf(Offset(0f, 0f)) }
    var fill by remember { mutableStateOf(false) }
    var swap by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(16.dp)) {

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .background(Color.Yellow)
        ) {
            drawPath(
                path = Path().apply {
                    if (swap) {
                        arcTo(
                            Rect(point21, point22),
                            startAngle2, sweepAngle2, moveTo2
                        )
                        arcTo(
                            Rect(point11, point12),
                            startAngle1, sweepAngle1, moveTo1
                        )
                    } else {
                        arcTo(
                            Rect(point11, point12),
                            startAngle1, sweepAngle1, moveTo1
                        )
                        arcTo(
                            Rect(point21, point22),
                            startAngle2, sweepAngle2, moveTo2
                        )
                    }
                },
                color = Color.Black,
                style = if (fill) Fill else Stroke(2.dp.value)
            )
            drawPoints(
                listOf(
                    point11, point12,
                    point21, point22
                ),
                PointMode.Points,
                Color.Red,
                strokeWidth = 8.dp.toPx(),
                cap = StrokeCap.Round
            )
        }
        setupArc(
            width,
            height,
            0f,
            0f,
            width.value,
            height.value / 2,
            90f, 180f
        ) { startAngle, sweepAngle, moveTo, offset1, offset2 ->
            startAngle1 = startAngle
            sweepAngle1 = sweepAngle
            moveTo1 = moveTo
            point11 = offset1
            point12 = offset2
        }
        setupArc(
            width, height,
            0f,
            height.value / 2,
            width.value,
            height.value,
            270f, 180f
        ) { startAngle, sweepAngle, moveTo, offset1, offset2 ->
            startAngle2 = startAngle
            sweepAngle2 = sweepAngle
            moveTo2 = moveTo
            point21 = offset1
            point22 = offset2
        }
        Row {
            Row(modifier = Modifier.weight(1f)) {
                Text(text = "Fill: ")
                Switch(checked = fill, onCheckedChange = { fill = it })
            }
            Row(modifier = Modifier.weight(1f)) {
                Text(text = "Swap: ")
                Switch(checked = swap, onCheckedChange = { swap = it })
            }
        }

    }
}

@Composable
private fun toPx(value: Float): Float {
    return LocalDensity.current.run { value.dp.toPx() }
}

@Composable
private fun setupArc(
    width: Dp,
    height: Dp,
    startX: Float, startY: Float,
    endX: Float, endY: Float,
    start: Float, sweep: Float,
    update: (startAngle: Float, sweepAngle: Float, moveTo: Boolean, offset1: Offset, offset2: Offset) -> Unit
) {
    var x1Position by remember { mutableStateOf(startX) }
    var y1Position by remember { mutableStateOf(startY) }
    var x2Position by remember { mutableStateOf(endX) }
    var y2Position by remember { mutableStateOf(endY) }
    var startAngle by remember { mutableStateOf(start) }
    var sweepAngle by remember { mutableStateOf(sweep) }
    var moveTo by remember { mutableStateOf(false) }

    val x1 = toPx(x1Position)
    val y1 = toPx(y1Position)
    val x2 = toPx(x2Position)
    val y2 = toPx(y2Position)

    update(
        startAngle,
        sweepAngle,
        moveTo,
        Offset(x1, y1),
        Offset(x2, y2),
    )

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
    Row {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = "Start Angle: ${startAngle.roundToInt()}")
            Slider(value = startAngle,
                valueRange = -360f..360f,
                onValueChange = {
                    startAngle = it
                }
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(text = "Sweep Angle: ${sweepAngle.roundToInt()}")
            Slider(value = sweepAngle,
                valueRange = -360f..360f,
                onValueChange = {
                    sweepAngle = it
                }
            )
        }
    }
    Row {
        Text(text = "MoveTo: ")
        Switch(checked = moveTo, onCheckedChange = { moveTo = it })
    }

}
