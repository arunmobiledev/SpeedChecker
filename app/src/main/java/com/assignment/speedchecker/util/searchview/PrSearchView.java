/*
 * Copyright (c) 2021. Arun Raju. All rights reserved
 */

package com.assignment.speedchecker.util.searchview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.appcompat.widget.AppCompatEditText;

public class PrSearchView extends AppCompatEditText {

    public interface DrawableClickListener {
        enum DrawablePosition { TOP, BOTTOM, START, END }
        void onClick(DrawablePosition target);
    }

    int actionX, actionY;
    private Drawable drawableEndBu;
    private Drawable drawableEnd;
    private Drawable drawableStart;
    private Drawable drawableTop;
    private Drawable drawableBottom;
    private DrawableClickListener clickListener;

    public PrSearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // this Contructure required when you are using this view in xml
    }

    public PrSearchView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    public void setCompoundDrawables(Drawable left, Drawable top,
                                     Drawable right, Drawable bottom) {
        if (left != null) {
            drawableStart = left;
        }
        if (right != null) {
            drawableEnd = right;
            drawableEndBu = right;
        }
        if (top != null) {
            drawableTop = top;
        }
        if (bottom != null) {
            drawableBottom = bottom;
        }
        super.setCompoundDrawables(left, top, right, bottom);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Rect bounds;
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            actionX = (int) event.getX();
            actionY = (int) event.getY();
            if (drawableBottom != null && drawableBottom.getBounds().contains(actionX, actionY)) {
                clickListener.onClick(DrawableClickListener.DrawablePosition.BOTTOM);
                return super.onTouchEvent(event);
            }

            if (drawableTop != null && drawableTop.getBounds().contains(actionX, actionY)) {
                clickListener.onClick(DrawableClickListener.DrawablePosition.TOP);
                return super.onTouchEvent(event);
            }

            // this works for left since container shares 0,0 origin with bounds
            if (drawableStart != null) {
                bounds = drawableStart.getBounds();

                int x, y;
                int extraTapArea = (int) (13 * getResources().getDisplayMetrics().density + 0.5);

                x = actionX;
                y = actionY;

                if (!bounds.contains(actionX, actionY)) {
                    /* Gives the +20 area for tapping. */
                    x = (int) (actionX - extraTapArea);
                    y = (int) (actionY - extraTapArea);

                    if (x <= 0)
                        x = actionX;
                    if (y <= 0)
                        y = actionY;

                    /* Creates square from the smallest value */
                    if (x < y) {
                        y = x;
                    }
                }

                if (bounds.contains(x, y) && clickListener != null) {
                    clickListener.onClick(DrawableClickListener.DrawablePosition.START);
                    event.setAction(MotionEvent.ACTION_CANCEL);
                    return false;

                }
            }

            if (drawableEnd != null) {
                bounds = drawableEnd.getBounds();

                int x, y;
                int extraTapArea = 13;

                /*
                 * IF USER CLICKS JUST OUT SIDE THE RECTANGLE OF THE DRAWABLE
                 * THAN ADD X AND SUBTRACT THE Y WITH SOME VALUE SO THAT AFTER
                 * CALCULATING X AND Y CO-ORDINATE LIES INTO THE DRAWBABLE
                 * BOUND. - this process help to increase the tappable area of
                 * the rectangle.
                 */
                x = (int) (actionX + extraTapArea);
                y = (int) (actionY - extraTapArea);

                /* Since this is right drawable subtract the value of x from the width
                 * of view. so that width - tappedarea will result in x co-ordinate in drawable bound.
                 */
                x = getWidth() - x;

                /*x can be negative if user taps at x co-ordinate just near the width.
                 * e.g views width = 300 and user taps 290. Then as per previous calculation
                 * 290 + 13 = 303. So subtract X from getWidth() will result in negative value.
                 * So to avoid this add the value previous added when x goes negative.
                 */

                if (x <= 0) {
                    x += extraTapArea;
                }

                /* If result after calculating for extra tappable area is negative.
                 * assign the original value so that after subtracting
                 * extra tapping area value doesn't go into negative value.
                 */

                if (y <= 0)
                    y = actionY;

                /*
                 * If drawable bounds contains the x and y points then move ahead.
                 * */
                if (bounds.contains(x, y) && clickListener != null) {
                    clickListener.onClick(DrawableClickListener.DrawablePosition.END);
                    event.setAction(MotionEvent.ACTION_CANCEL);
                    return false;
                }
                return super.onTouchEvent(event);
            }

        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void finalize() throws Throwable {
        drawableEnd = null;
        drawableBottom = null;
        drawableStart = null;
        drawableTop = null;
        super.finalize();
    }

    public void setDrawableClickListener(DrawableClickListener listener) {
        this.clickListener = listener;
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        if(text.toString().equalsIgnoreCase("")) {
            setCompoundDrawables(drawableStart, drawableTop, null, drawableBottom);
        } else {
            setCompoundDrawables(drawableStart, drawableTop, drawableEndBu, drawableBottom);
        }
    }
}
