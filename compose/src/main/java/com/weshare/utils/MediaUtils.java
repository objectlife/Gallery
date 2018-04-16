package com.weshare.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;

/**
 * Created by mrsimple on 12/4/2018.
 */

public final class MediaUtils {
    private MediaUtils() {
    }

    public static Bitmap getVideoThumb(Context context, Uri uri) {
        MediaMetadataRetriever mediaMetadataRetriever = null;
        Bitmap bitmap = null;
        try {
            mediaMetadataRetriever = new MediaMetadataRetriever();
            mediaMetadataRetriever.setDataSource(context, uri);
            bitmap = mediaMetadataRetriever.getFrameAtTime();
            if (mediaMetadataRetriever != null) {
                mediaMetadataRetriever.release();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (mediaMetadataRetriever != null) {
                mediaMetadataRetriever.release();
            }
        }
        return bitmap;
    }

}
