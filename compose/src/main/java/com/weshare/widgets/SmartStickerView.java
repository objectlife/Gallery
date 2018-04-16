/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.weshare.widgets;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.xiaopo.flying.sticker.StickerView;


/**
 * Created by mrsimple on 14/4/2018.
 */

public class SmartStickerView extends StickerView {

    private Handler mHandler = new Handler(Looper.getMainLooper()) ;

    public SmartStickerView(Context context) {
        super(context);
    }

    public SmartStickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SmartStickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected boolean onTouchDown(@NonNull MotionEvent event) {
        boolean isTouchDown = super.onTouchDown(event);
        // show the corner icons
        if ( isTouchDown && !showIcons ) {
            showIcons = true ;
            postInvalidate();
        }
        return isTouchDown ;
    }

    @Override
    protected void onTouchUp(@NonNull MotionEvent event) {
        super.onTouchUp(event);
        // hide the corner icons
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                showIcons = false ;
                postInvalidate();
            }
        }, 2000);
    }
}
