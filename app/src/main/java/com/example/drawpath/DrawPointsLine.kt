package com.example.drawpath

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp

@Composable
fun DrawPointsLine() {

    val LINE = "Line"
    val QUAD = "Quad"
    val CUBIC = "Cubic"
    val height = 200.dp
    val radioOptions = listOf(LINE, QUAD, CUBIC)
    val (selectedOption, onOptionSelected) = remember { mutableStateOf(radioOptions[1]) }
    var points by remember { mutableStateOf(listOf<Offset>()) }
    var nextPoints by remember { mutableStateOf(listOf<Offset>()) }

    Column(modifier = Modifier.padding(16.dp)) {

        Spacer(modifier = Modifier.height(16.dp))

        val drawModifier = Modifier
            .fillMaxWidth()
            .height(height)
            .clipToBounds()
            .background(Color.Yellow)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        points = points + Offset(it.x, it.y);
                        nextPoints = listOf()
                    },
                    onDoubleTap = { /* Called on Double Tap */ },
                    onLongPress = { /* Called on Long Press */ },
                    onTap = { /* Called on Tap */ })
            }

        Canvas(
            modifier = drawModifier
        ) {
            drawPath(

                Path().apply {
                    points.forEachIndexed { index, item ->
                        if (index == 0) {
                            moveTo(item.x, item.y)
                        } else {
                            when (selectedOption) {
                                LINE -> lineTo(item.x, item.y)
                                QUAD -> {
                                    val pointX1 = points[index - 1].x
                                    val pointY1 = points[index - 1].y
                                    val pointX2: Float
                                    val pointY2: Float

                                    if (index == points.size - 1) {
                                        pointX2 = item.x
                                        pointY2 = item.y
                                    } else {
                                        pointX2 = (points[index - 1].x + item.x) / 2
                                        pointY2 = (points[index - 1].y + item.y) / 2
                                    }
                                    quadraticBezierTo(pointX1, pointY1, pointX2, pointY2)
                                }
                                CUBIC -> {
                                    cubicTo(
                                        (points[index - 1].x + item.x) / 2, points[index - 1].y,
                                        (points[index - 1].x + item.x) / 2, item.y,
                                        item.x, item.y
                                    )
                                }
                            }
                        }
                    }
                },
                Color.Black, style = Stroke(2.dp.value)
            )
            drawPoints(
                points,
                PointMode.Points,
                Color.Red,
                strokeWidth = 6.dp.toPx(),
                cap = StrokeCap.Round
            )
        }

        Row {
            Spacer(Modifier.weight(1f))
            Button(enabled = points.isNotEmpty(),
                onClick = {
                    points = listOf()
                    nextPoints = listOf()
                }) {
                Text("Clear Path")
            }
            Spacer(Modifier.weight(1f))
            Button(enabled = points.isNotEmpty(),
                onClick = {
                    nextPoints = nextPoints + points.last()
                    points = points.dropLast(1)
                }) {
                Text("Undo")
            }
            Spacer(Modifier.weight(1f))
            Button(enabled = nextPoints.isNotEmpty(),
                onClick = {
                    points = points + nextPoints.last()
                    nextPoints = nextPoints.dropLast(1)
                }) {
                Text("Redo")
            }
            Spacer(Modifier.weight(1f))
        }
        Column {
            radioOptions.forEach { text ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = (text == selectedOption),
                            onClick = {
                                onOptionSelected(text)
                            }
                        )
                        .padding(horizontal = 16.dp),
                    verticalAlignment = CenterVertically
                ) {
                    RadioButton(
                        selected = (text == selectedOption),
                        onClick = { onOptionSelected(text) }
                    )
                    Text(
                        text = text,
                        style = MaterialTheme.typography.body1.merge(),
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
            }
        }
    }
}