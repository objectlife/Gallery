package com.google.android.cameraview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by mrsimple on 18/4/2018.
 */

public class FocusView extends View {

    private boolean haveTouch = false;
    private Rect mTouchArea;
    private Paint paint;

    public FocusView(Context context) {
        this(context, null);
    }

    public FocusView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FocusView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(0xeed7d7d7);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);
        haveTouch = false;
    }


    public void setHaveTouch(boolean val, Rect rect) {
        haveTouch = val;
        mTouchArea = rect;
    }

    // Remove the square after some time
    final Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    public void onDraw(Canvas canvas) {
        if (haveTouch && mTouchArea != null ) {
            mHandler.removeCallbacksAndMessages(null);
            canvas.clipRect(mTouchArea) ;
            canvas.drawRect(mTouchArea.left, mTouchArea.top, mTouchArea.right, mTouchArea.bottom, paint);

            mHandler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    FocusView.this.setHaveTouch(false, null );
                    invalidate();
                }
            }, 1000);
        }
    }
}
