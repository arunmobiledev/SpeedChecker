/*
 * Copyright (c) 2021. Arun Raju. All rights reserved
 */

package com.assignment.speedchecker.util.progressloading.indicators;


import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.assignment.speedchecker.util.progressloading.Indicator;

import java.util.ArrayList;

/**
 * Created by Arun on 2020/06/24.
 */
public class LineScalePulseOutRapidIndicator extends Indicator {

    public static final float SCALE=1.0f;

    float[] scaleYFloats=new float[]{SCALE,
            SCALE,
            SCALE,
            SCALE,
            SCALE,};

    @Override
    public void draw(Canvas canvas, Paint paint) {
        float translateX=getWidth()/11f;
        float translateY=getHeight()/2f;
        for (int i = 0; i < 5; i++) {
            canvas.save();
            canvas.translate((2 + i * 2) * translateX - translateX / 2, translateY);
            canvas.scale(SCALE, scaleYFloats[i]);
            RectF rectF=new RectF(-translateX/2,-getHeight()/2.5f,translateX/2,getHeight()/2.5f);
            canvas.drawRoundRect(rectF, 5, 5, paint);
            canvas.restore();
        }
    }

    @Override
    public ArrayList<ValueAnimator> onCreateAnimators() {
        ArrayList<ValueAnimator> animators=new ArrayList<>();
        long[] delays=new long[]{400,200,0,200,400};
        for (int i = 0; i < 5; i++) {
            final int index=i;
            ValueAnimator scaleAnim=ValueAnimator.ofFloat(1,0.4f,1);
            scaleAnim.setDuration(1000);
            scaleAnim.setRepeatCount(-1);
            scaleAnim.setStartDelay(delays[i]);
            addUpdateListener(scaleAnim, animation -> {
                scaleYFloats[index] = (float) animation.getAnimatedValue();
                postInvalidate();
            });
            animators.add(scaleAnim);
        }
        return animators;
    }

}
