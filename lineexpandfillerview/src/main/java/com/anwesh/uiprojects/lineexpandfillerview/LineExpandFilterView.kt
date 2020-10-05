package com.anwesh.uiprojects.lineexpandfillerview

/**
 * Created by anweshmishra on 06/10/20.
 */

import android.view.View
import android.view.MotionEvent
import android.content.Context
import android.app.Activity
import android.graphics.Paint
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.RectF
import android.graphics.Path

val colors : Array<Int> = arrayOf(
        "#F44336",
        "#03A9F4",
        "#2196F3",
        "#009688",
        "#FFC107"
).map({Color.parseColor(it)}).toTypedArray()
val parts : Int = 3
val strokeFactor : Int = 90
val scGap : Float = 0.02f / parts
val delay : Long = 20
val backColor : Int = Color.parseColor("#BDBDBD")

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n
fun Float.sinify() : Float = Math.sin(this * Math.PI).toFloat()
fun Int.iaf() : Float = 1f - 2 * (this % 2)

fun Canvas.drawLineExpandFiller(scale : Float, w : Float, h : Float, paint : Paint) {
    val sf : Float = scale.sinify()
    for (j in 0..2) {
        val ij : Int = j / 2
        val sfj : Float = sf.divideScale(ij, parts)
        val y : Float = h * 0.5f * ij
        val finalY : Float = y + h * 0.5f * sfj
        drawLine(w / 2, y, w / 2 - (ij * (w / 2) * sfj * (j.iaf())), finalY, paint)
    }
    save()
    val path : Path = Path()
    path.moveTo(w / 2, h / 2)
    path.lineTo(0f, h)
    path.lineTo(w, h)
    path.lineTo(w / 2, h / 2)
    clipPath(path)
    drawRect(RectF(0f, h / 2, w, h / 2 * (1 + sf.divideScale(2, parts))), paint)
    restore()
}

fun Canvas.drawLEFNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    paint.color = colors[i]
    paint.strokeCap = Paint.Cap.ROUND
    paint.strokeWidth = Math.min(w, h) / strokeFactor
    drawLineExpandFiller(scale, w, h, paint)
}

class LineExpandFilterView(ctx : Context) : View(ctx) {

    override fun onDraw(canvas : Canvas) {

    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

            }
        }
        return true
    }
}