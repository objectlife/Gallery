package com.weshare.domain;

import android.provider.MediaStore;

/**
 * Created by mrsimple on 17/4/2018.
 */

public class MediaItem {

    public long id ;
    public int type ;
    public String path ;

    private MediaItem() {
    }

    public static MediaItem create(long id, int type, String filePath) {
        MediaItem mediaItem = new MediaItem();
        mediaItem.id = id ;
        mediaItem.type = type;
        mediaItem.path = filePath ;
        return mediaItem ;
    }

    public boolean isVideo() {
        return type == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO ;
    }
}
