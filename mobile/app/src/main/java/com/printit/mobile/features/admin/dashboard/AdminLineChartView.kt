package com.printit.mobile.features.admin.dashboard

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class AdminLineChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private val values = listOf(3f, 1f, 2f, 6f, 3f, 2f, 1f)
    private val labels = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#9B2C3A")
        strokeWidth = 7f
        style = Paint.Style.STROKE
    }

    private val pointPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#9B2C3A")
        style = Paint.Style.FILL
    }

    private val gridPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#E5E7EB")
        strokeWidth = 2f
        style = Paint.Style.STROKE
    }

    private val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#475467")
        textSize = 28f
        textAlign = Paint.Align.CENTER
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val left = 42f
        val right = width - 32f
        val top = 30f
        val bottom = height - 44f
        val chartHeight = bottom - top
        val chartWidth = right - left
        val max = values.maxOrNull() ?: 1f
        val gap = chartWidth / (values.size - 1)

        for (i in 0..3) {
            val y = top + (chartHeight / 3f) * i
            canvas.drawLine(left, y, right, y, gridPaint)
        }

        val points = values.mapIndexed { index, value ->
            val x = left + gap * index
            val y = bottom - ((value / max) * chartHeight)
            Pair(x, y)
        }

        for (i in 0 until points.size - 1) {
            canvas.drawLine(
                points[i].first,
                points[i].second,
                points[i + 1].first,
                points[i + 1].second,
                linePaint
            )
        }

        points.forEach {
            canvas.drawCircle(it.first, it.second, 10f, pointPaint)
        }

        labels.forEachIndexed { index, label ->
            val x = left + gap * index
            canvas.drawText(label, x, height - 10f, labelPaint)
        }
    }
}