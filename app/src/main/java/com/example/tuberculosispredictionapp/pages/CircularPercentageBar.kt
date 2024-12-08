package com.example.tuberculosispredictionapp.pages

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp

@Composable
fun CircularPercentageBar(
    percentage: Float,
    color: Color,
    size: Dp,
    strokeWidth: Dp,
    text: String,
    textSize: Int
) {
    Box(
        contentAlignment = androidx.compose.ui.Alignment.Center,
        modifier = Modifier.size(size)
    ) {
        Canvas(modifier = Modifier.size(size)) {
            drawArc(
                color = Color(0xFF81C784),
                startAngle = -90f,
                sweepAngle = 360 * percentage,
                useCenter = false,
                style = Stroke(strokeWidth.toPx())
            )
        }
        Text(text = text,
            fontSize = textSize.sp,
            color = Color.Black,
            style = TextStyle(fontFamily = customRobotoFontFamily,
                fontWeight = FontWeight.Bold)
        )
    }
}
