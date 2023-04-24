package com.example.drawpath

import android.graphics.Bitmap
import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import java.lang.Float.max

@Composable
fun DrawPathArcDialog() {
    val height = 200.dp
    var text by rememberSaveable { mutableStateOf("Testing") }

    Column(modifier = Modifier.padding(16.dp)) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .background(Color.Yellow)
        ) {
            val bitmap = setCustomMapIcon(text)
            drawImage(bitmap.asImageBitmap(), topLeft = Offset(
                (size.width - bitmap.width)/2,(size.height - bitmap.height)/2)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        TextField (
            modifier = Modifier.fillMaxWidth(),
            value = text,
            onValueChange = {
                text = it
            },
            label = { Text("Label") }
        )
    }
}

private fun setCustomMapIcon(message: String): Bitmap {
    val height = 150f
    val widthPadding = 80.dp.value
    val width = max(200f, paintTextWhite.measureText(message, 0, message.length) + widthPadding)
    val roundStart = height/3
    val path = android.graphics.Path().apply {
        arcTo(0f, 0f,
            roundStart * 2, roundStart * 2,
            -90f, -180f, true)
        lineTo(width/2 - roundStart / 2, height * 2/3)
        lineTo(width/2, height)
        lineTo(width/2 + roundStart / 2, height * 2/3)
        lineTo(width - roundStart, height * 2/3)
        arcTo(width - roundStart * 2, 0f,
            width, height * 2/3,
            90f, -180f, true)
        lineTo(roundStart, 0f)
    }

    val bm = Bitmap.createBitmap(width.toInt(), height.toInt(), Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(bm)
    canvas.drawPath(path, paintBlackFill)
    canvas.drawPath(path, paintWhite)
    canvas.drawText(message, width/2, height * 2/3 * 2/3, paintTextWhite)
    return bm
}

val paintBlackFill = Paint().apply {
    style = Paint.Style.STROKE
    strokeCap = Paint.Cap.ROUND
    strokeJoin = Paint.Join.ROUND
    isAntiAlias = true
    color = android.graphics.Color.DKGRAY
    style = Paint.Style.FILL
    textAlign = Paint.Align.CENTER
    textSize = 30.dp.value
}

val paintTextWhite = Paint().apply {
    strokeCap = Paint.Cap.ROUND
    strokeJoin = Paint.Join.ROUND
    isAntiAlias = true
    color = android.graphics.Color.WHITE
    textAlign = Paint.Align.CENTER
    strokeWidth = 6.dp.value
    textSize = 48.dp.value
}

val paintWhite = Paint().apply {
    style = Paint.Style.STROKE
    strokeCap = Paint.Cap.ROUND
    strokeJoin = Paint.Join.ROUND
    isAntiAlias = true
    color = android.graphics.Color.WHITE
    strokeWidth = 6.dp.value
}
