package com.example.drawpath

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt
import kotlin.math.sqrt

@Composable
fun DrawPointsLine() {

    val LINE = "Line"
    val QUAD = "Quad"
    val CUBIC = "Cubic"
    val HYBRID = "Hybrid (Quad + Cubic)"
    val CUBICADVANCED = "Cubic Advanced"
    val height = 200.dp
    val radioOptions = listOf(LINE, QUAD, CUBIC, HYBRID, CUBICADVANCED)
    val (selectedOption, onOptionSelected) = remember { mutableStateOf(radioOptions[1]) }
    var points by remember { mutableStateOf(listOf<Offset>()) }
    var nextPoints by remember { mutableStateOf(listOf<Offset>()) }
    var showAnchorPoints by remember { mutableStateOf(false) }
    var advancedMiddleSlope by remember { mutableStateOf(true) }
    var weightedMiddleX by remember { mutableStateOf(2f) }

    Column(modifier = Modifier.padding(16.dp)) {

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
                                    drawQuad(points, index, item, showAnchorPoints, this)
                                }
                                CUBIC -> {
                                    drawSimpleCubic(points, index, item, showAnchorPoints, this)
                                }
                                HYBRID -> {
                                    val triple = extractDyDx(points, index, item)
                                    val currentPointDyDx = triple.first
                                    val nextPointDyDx = triple.second
                                    val prevPointDyDx = triple.third

                                    if (shouldDrawCube(
                                            currentPointDyDx.slope,
                                            prevPointDyDx?.slope,
                                            nextPointDyDx?.slope
                                        )
                                    ) {
                                        drawSimpleCubic(points, index, item, showAnchorPoints, this)
                                    } else {
                                        drawQuad(points, index, item, showAnchorPoints, this)
                                    }
                                }
                                CUBICADVANCED -> {
                                    drawAdvancedCubic(
                                        points,
                                        index,
                                        item,
                                        showAnchorPoints,
                                        advancedMiddleSlope,
                                        weightedMiddleX,
                                        this
                                    )
                                }
                            }
                        }
                    }
                },
                Color.Black, style = Stroke(2.dp.value)
            )
            drawPoints(points, Color.Red, 6.dp)
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
        Row(verticalAlignment = CenterVertically) {
            Spacer(Modifier.width(16.dp))
            Switch(checked = showAnchorPoints, onCheckedChange = { showAnchorPoints = it })
            Text(text = "Show Anchor Points")
        }
        Row(verticalAlignment = CenterVertically) {
            Spacer(Modifier.width(16.dp))
            Switch(checked = advancedMiddleSlope, onCheckedChange = { advancedMiddleSlope = it })
            Text(text = "Advanced Middle Slope (Cubic Advanced)")
            Spacer(Modifier.weight(1f))
        }
        Column(Modifier.padding(16.dp)) {
            Text(text = "Weighted Middle X (Cubic Advanced): ${weightedMiddleX.roundToInt()}")
            Slider(value = weightedMiddleX,
                valueRange = 1f..3f,
                steps = 1,
                onValueChange = {
                    weightedMiddleX = it
                }
            )
        }
    }
}

private fun DrawScope.drawAdvancedCubic(
    points: List<Offset>,
    index: Int,
    item: Offset,
    showAnchorPoints: Boolean,
    advancedMiddleSlope: Boolean,
    weightedMiddleX: Float,
    path: Path
) {
    val triple = extractDyDx(points, index, item)
    val currentPointDyDx = triple.first
    val nextPointDyDx = triple.second
    val prevPointDyDx = triple.third

    val prevX = points[index - 1].x
    val prevY = points[index - 1].y
    val (x1, y1) = calculateMiddleCoordinate(prevPointDyDx, currentPointDyDx, advancedMiddleSlope,
        prevX, prevY, item.x, item.y, weightedMiddleX)
    val (x2, y2) = calculateMiddleCoordinate(nextPointDyDx, currentPointDyDx, advancedMiddleSlope,
        item.x, item.y, prevX, prevY, weightedMiddleX)


    if (showAnchorPoints) {
        drawPoints(listOf(Offset(x1, y1)), Color.Green, 4.dp)
        drawPoints(listOf(Offset(x2, y2)), Color.Blue, 4.dp)
    }

    path.cubicTo(
        x1, y1,
        x2, y2,
        item.x, item.y
    )
}

private fun calculateMiddleCoordinate(
    referenceDyDx: SlopeDirection?,
    currentPointDyDx: SlopeDirection,
    advancedMiddleSlope: Boolean,
    focusX: Float,
    focusY: Float,
    referenceX: Float,
    referenceY: Float,
    weightedMiddleX: Float
) : Pair<Float, Float> {
    val middleSlope = calculateMiddleSlope(referenceDyDx?.slope, currentPointDyDx.slope, advancedMiddleSlope)

    val shouldInverse = if (referenceDyDx?.forward == null) false
    else (referenceDyDx.forward xor currentPointDyDx.forward)

    return if (shouldInverse) {
        val inverseSlope = -1/avoidZero(middleSlope)
        val middleY = (focusY * weightedMiddleX + referenceY) / (weightedMiddleX + 1)
        val c = focusY - inverseSlope * focusX
        Pair((middleY - c)/inverseSlope, middleY)
    } else {
        val middleX = (focusX * weightedMiddleX + referenceX) / (weightedMiddleX + 1)
        val c = focusY - middleSlope * focusX
        Pair(middleX, middleSlope * middleX + c)
    }
}

private fun calculateMiddleSlope(
    referenceDyDx: Float?,
    currentPointDyDx: Float,
    advancedMiddleSlope: Boolean
): Float {

    if (referenceDyDx == null) return currentPointDyDx

    if (!advancedMiddleSlope) {
        return (referenceDyDx + currentPointDyDx) / 2
    }

    val denominator =
        1 - referenceDyDx * currentPointDyDx + sqrt(
            (1 + referenceDyDx * referenceDyDx) * (1 + currentPointDyDx * currentPointDyDx)
        )
    val numerator = referenceDyDx + currentPointDyDx

    return numerator / avoidZero(denominator)
}

private fun avoidZero(value: Float): Float {
    return if (value == 0f) 0.000000000000000000001f else value
}

private fun DrawScope.drawSimpleCubic(
    points: List<Offset>,
    index: Int,
    item: Offset,
    showAnchorPoints: Boolean,
    path: Path
) {
    val prevX = points[index - 1].x
    val prevY = points[index - 1].y
    val middleX = (prevX + item.x) / 2

    if (showAnchorPoints) {
        drawPoints(listOf(Offset(middleX, prevY)), Color.Green, 4.dp)
        drawPoints(listOf(Offset(middleX, item.y)), Color.Blue, 4.dp)
    }

    path.cubicTo(
        middleX, prevY,
        middleX, item.y,
        item.x, item.y
    )
}

private fun DrawScope.drawPoints(
    points: List<Offset>,
    color: Color,
    strokeWidth: Dp
) {
    drawPoints(
        points,
        PointMode.Points,
        color,
        strokeWidth = strokeWidth.toPx(),
        cap = StrokeCap.Round
    )
}

private fun DrawScope.drawQuad(
    points: List<Offset>,
    index: Int,
    item: Offset,
    showAnchorPoints: Boolean,
    path: Path
) {
    val prevX = points[index - 1].x
    val prevY = points[index - 1].y
    val plotX: Float
    val plotY: Float
    if (index == points.size - 1) {
        plotX = item.x
        plotY = item.y
    } else {
        plotX = (prevX + item.x) / 2
        plotY = (prevY + item.y) / 2
    }

    if (showAnchorPoints) {
        drawPoints(listOf(Offset(plotX, plotY)), Color.Blue, 4.dp)
    }
    path.quadraticBezierTo(
        prevX, prevY,
        plotX, plotY
    )
}

private fun extractDyDx(
    points: List<Offset>,
    index: Int,
    item: Offset,
): Triple<SlopeDirection, SlopeDirection?, SlopeDirection?> {
    var prevPointDyDx1: SlopeDirection? = null
    var nextPointDyDx1: SlopeDirection? = null
    val currentPointDyDx1 = SlopeDirection(
        dydx(
            points[index - 1].x, points[index - 1].y,
            item.x, item.y
        ), item.x - points[index - 1].x > 0
    )

    if (index > 1) {
        prevPointDyDx1 = SlopeDirection(
            dydx(
                points[index - 1].x, points[index - 1].y,
                points[index - 2].x, points[index - 2].y,
            ), points[index - 1].x - points[index - 2].x > 0
        )
    }

    if (index < points.size - 1) {
        nextPointDyDx1 = SlopeDirection(
            dydx(
                points[index + 1].x, points[index + 1].y,
                item.x, item.y
            ), points[index + 1].x - item.x > 0
        )
    }
    return Triple(currentPointDyDx1, nextPointDyDx1, prevPointDyDx1)
}

data class SlopeDirection(val slope: Float, val forward: Boolean)

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

private fun dydx(x1: Float, y1: Float, x2: Float, y2: Float): Float {
    val dY = y2 - y1
    val dX = avoidZero(x2 - x1)
    return dY / dX
}
