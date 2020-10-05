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
        val ij : Int = (j + 1) / 2
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

    private val renderer : Renderer = Renderer(this)

    override fun onDraw(canvas : Canvas) {
        renderer.render(canvas)
    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                renderer.handleTap()
            }
        }
        return true
    }

    data class State(var scale : Float = 0f, var dir : Float = 0f, var prevScale : Float = 0f) {

        fun update(cb : (Float) -> Unit) {
            scale += scGap * dir
            if (Math.abs(scale - prevScale) > 1) {
                scale = prevScale + dir
                dir = 0f
                prevScale = scale
                cb(prevScale)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            if (dir == 0f) {
                dir = 1f - 2 * prevScale
                cb()
            }
        }
    }

    data class Animator(var view : View, var animated : Boolean = false) {

        fun animate(cb : () -> Unit) {
            if (animated) {
                cb()
                try {
                    Thread.sleep(delay)
                    view.invalidate()
                } catch(ex : Exception) {

                }
            }
        }

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }
    }

    data class LEFNode(var i : Int, val state : State = State()) {


        private var prev : LEFNode? = null
        private var next : LEFNode? = null

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            if (i < colors.size - 1) {
                next = LEFNode(i + 1)
                next?.prev = this
            }
        }

        fun draw(canvas : Canvas, paint : Paint) {
            canvas.drawLEFNode(i, state.scale, paint)
        }

        fun update(cb : (Float) -> Unit) {
            state.update(cb)
        }

        fun startUpdating(cb : () -> Unit) {
            state.startUpdating(cb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : LEFNode {
            var curr : LEFNode? = prev
            if (dir == 1) {
                curr = next
            }
            if (curr != null) {
                return curr
            }
            cb()
            return this
        }
    }

    data class LineExpandFiller(var i : Int) {

        private var curr : LEFNode = LEFNode(0)
        private var dir : Int = 1

        fun draw(canvas : Canvas, paint : Paint) {
            curr.draw(canvas, paint)
        }

        fun update(cb : (Float) -> Unit) {
            curr.update {
                curr = curr.getNext(dir) {
                    dir *= -1
                }
                cb(it)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            curr.startUpdating(cb)
        }
    }

    data class Renderer(var view : LineExpandFilterView) {

        private val animator : Animator = Animator(view)
        private val lef : LineExpandFiller = LineExpandFiller(0)
        private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

        fun render(canvas : Canvas) {
            canvas.drawColor(backColor)
            lef.draw(canvas, paint)
            animator.animate {
                lef.update {
                    animator.stop()
                }
            }
        }

        fun handleTap() {
            lef.startUpdating {
                animator.start()
            }
        }
    }

    companion object {

        fun create(activity : Activity) : LineExpandFilterView {
            val view : LineExpandFilterView = LineExpandFilterView(activity)
            activity.setContentView(view)
            return view
        }
    }
}