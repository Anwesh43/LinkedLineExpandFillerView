package com.anwesh.uiprojects.lineexpandfillerview

/**
 * Created by anweshmishra on 06/10/20.
 */

import android.view.View
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
val parts : Int = 5
val srokeFactor : Int = 90
val scGap : Float = 0.02f / parts
val delay : Long = 20
val backColor : Int = Color.parseColor("#BDBDBD")
