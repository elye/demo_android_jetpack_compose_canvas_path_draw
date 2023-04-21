package com.example.drawpath

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.Absolute.Center
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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

    Column(modifier = Modifier.padding(16.dp)) {

        Spacer(modifier = Modifier.height(16.dp))

        val drawModifier = Modifier
            .fillMaxWidth()
            .height(height)
            .clipToBounds()
            .background(Color.Yellow)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = { points = points + Offset(it.x, it.y) },
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
                                    if (index == points.size - 1) {
                                        quadraticBezierTo(
                                            points.get(index - 1).x,
                                            points.get(index - 1).y,
                                             item.x, item.y
                                        )
                                    } else {
                                        quadraticBezierTo(
                                            points.get(index - 1).x,
                                            points.get(index - 1).y,
                                            (points.get(index - 1).x + item.x) / 2,
                                            (points.get(index - 1).y + item.y) / 2,
                                        )
                                    }
                                }
                                CUBIC -> {
                                    cubicTo(
                                        (points.get(index - 1).x + item.x) / 2,
                                        points.get(index - 1).y,
                                        (points.get(index - 1).x + item.x) / 2,
                                        item.y,
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

        Button(onClick = {
            points = listOf()
        }) {

            Text("Clear Path")
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
