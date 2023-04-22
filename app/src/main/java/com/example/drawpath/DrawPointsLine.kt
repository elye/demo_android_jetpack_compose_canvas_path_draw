package com.example.drawpath

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
    val HYBRID = "Hybrid"
    val height = 200.dp
    val radioOptions = listOf(LINE, QUAD, CUBIC, HYBRID)
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
                                LINE -> {
                                    lineTo(item.x, item.y)
                                }
                                QUAD -> {
                                    if (index == points.size - 1) {
                                        quadraticBezierTo(
                                            points[index - 1].x,
                                            points[index - 1].y,
                                            item.x, item.y
                                        )
                                    } else {
                                        quadraticBezierTo(
                                            points[index - 1].x,
                                            points[index - 1].y,
                                            (points[index - 1].x + item.x) / 2,
                                            (points[index - 1].y + item.y) / 2
                                        )
                                    }
                                }
                                CUBIC -> {
                                    cubicTo(
                                        (points[index - 1].x + item.x) / 2, points[index - 1].y,
                                        (points[index - 1].x + item.x) / 2, item.y,
                                        item.x, item.y
                                    )
                                }
                                HYBRID -> {
                                    val prevPointDyDx: Float?
                                    val nextPointDyDx: Float?
                                    val currentPointDyDx: Float

                                    val triple = extractDyDx(points, index, item)
                                    currentPointDyDx = triple.first
                                    nextPointDyDx = triple.second
                                    prevPointDyDx = triple.third

                                    if (shouldDrawCube(currentPointDyDx, prevPointDyDx, nextPointDyDx)) {
                                        cubicTo(
                                            (points[index - 1].x + item.x) / 2, points[index - 1].y,
                                            (points[index - 1].x + item.x) / 2, item.y,
                                            item.x, item.y
                                        )
                                    } else {
                                        if (shouldDrawQuadEnd(currentPointDyDx, prevPointDyDx, nextPointDyDx)) {
                                            quadraticBezierTo(
                                                points[index - 1].x,
                                                points[index - 1].y,
                                                item.x, item.y
                                            )
                                        } else {
                                            quadraticBezierTo(
                                                points[index - 1].x,
                                                points[index - 1].y,
                                                (points[index - 1].x + item.x) / 2,
                                                (points[index - 1].y + item.y) / 2
                                            )
                                        }
                                    }
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
                    nextPoints = nextPoints + points.reversed()
                    points = listOf()
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

private fun extractDyDx(
    points: List<Offset>,
    index: Int,
    item: Offset,
): Triple<Float, Float?, Float?> {
    var prevPointDyDx1 : Float? = null
    var nextPointDyDx1 : Float? = null
    val currentPointDyDx1 = dydx(
        points[index - 1].x, points[index - 1].y,
        item.x, item.y
    )

    if (index > 1) {
        prevPointDyDx1 = dydx(
            points[index - 1].x, points[index - 1].y,
            points[index - 2].x, points[index - 2].y,
        )
    }

    if (index < points.size - 1) {
        nextPointDyDx1 = dydx(
            points[index + 1].x, points[index + 1].y,
            item.x, item.y
        )
    }
    return Triple(currentPointDyDx1, nextPointDyDx1, prevPointDyDx1)
}

fun shouldDrawCube(currentPointDyDx: Float, prevPointDyDx: Float?, nextPointDyDx: Float?): Boolean {
    return if (prevPointDyDx == null) {
        if (nextPointDyDx == null) {
            false
        } else {
            (currentPointDyDx > 0 && nextPointDyDx < 0) || (currentPointDyDx < 0 && nextPointDyDx > 0)
        }
    } else if (nextPointDyDx == null) {
        (currentPointDyDx > 0 && prevPointDyDx < 0) || (currentPointDyDx < 0 && prevPointDyDx > 0)
    } else {
        (currentPointDyDx > 0 && prevPointDyDx < 0 && nextPointDyDx < 0) ||
                (currentPointDyDx < 0 && prevPointDyDx > 0 && nextPointDyDx > 0)
    }
}

fun shouldDrawQuadEnd(currentPointDyDx: Float, prevPointDyDx: Float?, nextPointDyDx: Float?): Boolean {
    return if (prevPointDyDx == null) {
        nextPointDyDx == null
    } else if (nextPointDyDx == null) {
        (currentPointDyDx > 0 && prevPointDyDx > 0) || (currentPointDyDx < 0 && prevPointDyDx < 0)
    } else {
        (currentPointDyDx > 0 && prevPointDyDx > 0 && nextPointDyDx < 0) ||
                (currentPointDyDx < 0 && prevPointDyDx < 0 && nextPointDyDx > 0)
    }
}

private fun dydx(x1: Float, y1: Float, x2: Float, y2: Float): Float {
    return (x2 - x1) / (y2 - y1)
}
