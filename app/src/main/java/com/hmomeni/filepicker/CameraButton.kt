package com.hmomeni.filepicker

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.os.Build
import android.support.annotation.RequiresApi
import android.util.AttributeSet
import android.view.View

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class CameraButton(context: Context?, attributeSet: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : View(context, attributeSet, defStyleAttr, defStyleRes) {
    constructor(context: Context?, attributeSet: AttributeSet?, defStyleAttr: Int) : this(context, attributeSet, defStyleAttr, 0)
    constructor(context: Context?, attributeSet: AttributeSet?) : this(context, attributeSet, 0)
    constructor(context: Context?) : this(context, null)

    private val outerPaint = Paint().apply {
        color = Color.parseColor("#88FFFFFF")
    }
    private val innerPaint = Paint().apply {
        color = Color.WHITE
    }
    var isMeasured = false
    lateinit var midPoint: Point
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if ((measuredHeight > 0 || measuredWidth > 0) && !isMeasured) {
            isMeasured = true
            midPoint = measureViewCenter(Point(0, 0), Point(measuredWidth, measuredHeight))
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawCircle(midPoint.x.toFloat(), midPoint.y.toFloat(), ((measuredWidth / 2) * 0.8).toFloat(), outerPaint)
        canvas.drawCircle(midPoint.x.toFloat(), midPoint.y.toFloat(), ((measuredWidth / 2) * 0.6).toFloat(), innerPaint)
    }


    private fun measureViewCenter(p1: Point, p2: Point): Point {
        val cX = (p1.x + p2.x) / 2
        val cY = (p1.y + p2.y) / 2
        return Point(cX, cY)
    }

}