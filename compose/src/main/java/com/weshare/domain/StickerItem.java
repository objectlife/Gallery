package com.weshare.domain;

import android.text.TextUtils;

/**
 * Created by mrsimple on 17/4/2018.
 */

public class StickerItem {

    public static final int CREATE_TEXT = -1 ;
    public String resUrl = "";
    public int resId ;

    public StickerItem(String url) {
        this.resUrl = url;
    }

    public StickerItem(int resId) {
        this.resId = resId;
    }

    public boolean isCreateTextSticker() {
        return resId == CREATE_TEXT ;
    }

    public boolean isRemoteSticker() {
        return !TextUtils.isEmpty(resUrl) ;
    }
}
