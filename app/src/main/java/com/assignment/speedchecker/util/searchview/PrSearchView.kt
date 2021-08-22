/*
 * Copyright (c) 2021. Arun Raju. All rights reserved
 */
package com.assignment.speedchecker.util.searchview

import kotlin.Throws
import androidx.appcompat.widget.AppCompatEditText
import com.assignment.speedchecker.util.searchview.PrSearchView.DrawableClickListener.DrawablePosition
import android.graphics.drawable.Drawable
import android.annotation.SuppressLint
import android.view.MotionEvent
import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.util.AttributeSet

class PrSearchView : AppCompatEditText {
    interface DrawableClickListener {
        enum class DrawablePosition {
            TOP, BOTTOM, START, END
        }

        fun onClick(target: DrawablePosition?)
    }

    var actionX = 0
    var actionY = 0
    private var drawableEndBu: Drawable? = null
    private var drawableEnd: Drawable? = null
    private var drawableStart: Drawable? = null
    private var drawableTop: Drawable? = null
    private var drawableBottom: Drawable? = null
    private var clickListener: DrawableClickListener? = null

    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!, attrs
    ) {        // this Contructure required when you are using this view in xml
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context!!, attrs, defStyle
    ) {
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
    }

    override fun setCompoundDrawables(
        left: Drawable?, top: Drawable?,
        right: Drawable?, bottom: Drawable?
    ) {
        if (left != null) {
            drawableStart = left
        }
        if (right != null) {
            drawableEnd = right
            drawableEndBu = right
        }
        if (top != null) {
            drawableTop = top
        }
        if (bottom != null) {
            drawableBottom = bottom
        }
        super.setCompoundDrawables(left, top, right, bottom)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        var bounds: Rect
        if (event.action == MotionEvent.ACTION_DOWN) {
            actionX = event.x.toInt()
            actionY = event.y.toInt()
            if (drawableBottom != null && drawableBottom!!.bounds.contains(actionX, actionY)) {
                clickListener!!.onClick(DrawablePosition.BOTTOM)
                return super.onTouchEvent(event)
            }
            if (drawableTop != null && drawableTop!!.bounds.contains(actionX, actionY)) {
                clickListener!!.onClick(DrawablePosition.TOP)
                return super.onTouchEvent(event)
            }

            // this works for left since container shares 0,0 origin with bounds
            if (drawableStart != null) {
                bounds = drawableStart!!.bounds
                var x: Int
                var y: Int
                val extraTapArea = (13 * resources.displayMetrics.density + 0.5).toInt()
                x = actionX
                y = actionY
                if (!bounds.contains(actionX, actionY)) {
                    /* Gives the +20 area for tapping. */
                    x = (actionX - extraTapArea)
                    y = (actionY - extraTapArea)
                    if (x <= 0) x = actionX
                    if (y <= 0) y = actionY

                    /* Creates square from the smallest value */if (x < y) {
                        y = x
                    }
                }
                if (bounds.contains(x, y) && clickListener != null) {
                    clickListener!!.onClick(DrawablePosition.START)
                    event.action = MotionEvent.ACTION_CANCEL
                    return false
                }
            }
            if (drawableEnd != null) {
                bounds = drawableEnd!!.bounds
                var x: Int
                var y: Int
                val extraTapArea = 13

                /*
                 * IF USER CLICKS JUST OUT SIDE THE RECTANGLE OF THE DRAWABLE
                 * THAN ADD X AND SUBTRACT THE Y WITH SOME VALUE SO THAT AFTER
                 * CALCULATING X AND Y CO-ORDINATE LIES INTO THE DRAWBABLE
                 * BOUND. - this process help to increase the tappable area of
                 * the rectangle.
                 */x = (actionX + extraTapArea)
                y = (actionY - extraTapArea)

                /* Since this is right drawable subtract the value of x from the width
                 * of view. so that width - tappedarea will result in x co-ordinate in drawable bound.
                 */x = width - x

                /*x can be negative if user taps at x co-ordinate just near the width.
                 * e.g views width = 300 and user taps 290. Then as per previous calculation
                 * 290 + 13 = 303. So subtract X from getWidth() will result in negative value.
                 * So to avoid this add the value previous added when x goes negative.
                 */if (x <= 0) {
                    x += extraTapArea
                }

                /* If result after calculating for extra tappable area is negative.
                 * assign the original value so that after subtracting
                 * extra tapping area value doesn't go into negative value.
                 */if (y <= 0) y = actionY

                /*
                 * If drawable bounds contains the x and y points then move ahead.
                 * */if (bounds.contains(x, y) && clickListener != null) {
                    clickListener!!.onClick(DrawablePosition.END)
                    event.action = MotionEvent.ACTION_CANCEL
                    return false
                }
                return super.onTouchEvent(event)
            }
        }
        return super.onTouchEvent(event)
    }

    @Throws(Throwable::class)
    protected fun finalize() {
        drawableEnd = null
        drawableBottom = null
        drawableStart = null
        drawableTop = null
    }

    fun setDrawableClickListener(listener: DrawableClickListener) {
        clickListener = listener
    }

    override fun onTextChanged(
        text: CharSequence,
        start: Int,
        lengthBefore: Int,
        lengthAfter: Int
    ) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter)
        if (text.toString().equals("", ignoreCase = true)) {
            setCompoundDrawables(drawableStart, drawableTop, null, drawableBottom)
        } else {
            setCompoundDrawables(drawableStart, drawableTop, drawableEndBu, drawableBottom)
        }
    }
}