package com.printit.mobile.features.admin.dashboard

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class AdminBarChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private val values = listOf(0f, 0f, 0f, 0f, 0f, 130f)
    private val labels = listOf("Dec", "Jan", "Feb", "Mar", "Apr", "May")

    private val barPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#F4C400")
        style = Paint.Style.FILL
    }

    private val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#475467")
        textSize = 28f
        textAlign = Paint.Align.CENTER
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val left = 36f
        val right = width - 36f
        val top = 30f
        val bottom = height - 44f
        val chartHeight = bottom - top
        val chartWidth = right - left
        val max = (values.maxOrNull() ?: 1f).coerceAtLeast(1f)
        val gap = chartWidth / values.size
        val barWidth = gap * 0.45f

        values.forEachIndexed { index, value ->
            val centerX = left + gap * index + gap / 2f
            val barHeight = (value / max) * chartHeight
            val barTop = bottom - barHeight

            if (value > 0f) {
                canvas.drawRoundRect(
                    centerX - barWidth / 2f,
                    barTop,
                    centerX + barWidth / 2f,
                    bottom,
                    12f,
                    12f,
                    barPaint
                )
            }

            canvas.drawText(labels[index], centerX, height - 10f, labelPaint)
        }
    }
}