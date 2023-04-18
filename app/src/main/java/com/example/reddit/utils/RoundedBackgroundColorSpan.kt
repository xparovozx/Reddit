package com.example.reddit.utils

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.text.style.LineBackgroundSpan
import android.view.View

class RoundedBackgroundColorSpan(
    backgroundColor: Int,
    private val radius: Float,
    private val view: View
) : LineBackgroundSpan {
    private val rect = RectF()
    private val paint = Paint().apply {
        color = backgroundColor
        isAntiAlias = true
    }

    override fun drawBackground(
        c: Canvas,
        p: Paint,
        left: Int,
        right: Int,
        top: Int,
        baseline: Int,
        bottom: Int,
        text: CharSequence,
        start: Int,
        end: Int,
        lineNumber: Int
    ) {

        val actualWidth = p.measureText(text, start, end)
        if (text.toString().isTextFromRightToLeft()) {
            val shiftRight = view.width - 2f
            val actualShiftLeft = if (actualWidth <= 550f) view.width - actualWidth - 80f else
                view.width - actualWidth
            val actualTop = top.toFloat() + 16f
            val actualBottom = bottom.toFloat() - 6f
            rect.set(actualShiftLeft, actualTop, shiftRight, actualBottom)
            c.drawRoundRect(rect, radius, radius, paint)
        } else {
            val shiftLeft = 2f
            val shiftRight = if (actualWidth <= 550f) actualWidth + 80f
            else actualWidth - shiftLeft
            val actualTop = top.toFloat() + 16f
            val actualBottom = bottom.toFloat() - 6f
            rect.set(shiftLeft, actualTop, shiftRight, actualBottom)
            c.drawRoundRect(rect, radius, radius, paint)
        }
    }
}