package com.weshare.domain;

import android.provider.MediaStore;

/**
 * Created by mrsimple on 17/4/2018.
 */

public class MediaItem {

    public static final int TYPE_HEADER = 0;
    public static final int TYPE_ITEM = 1;


    public long id ;
    public int type ;
    public String path ;
    public long date;

    public int itemType;

    private MediaItem() {
    }

    public static MediaItem create(long id, int type, String filePath) {
        MediaItem mediaItem = new MediaItem();
        mediaItem.id = id ;
        mediaItem.type = type;
        mediaItem.path = filePath ;
        return mediaItem ;
    }

    public static MediaItem create(long id, int type, String filePath, long date) {
        MediaItem mediaItem = create(id, type, filePath);
        mediaItem.date = date;
        return mediaItem ;
    }

    public static MediaItem createHeaderItem(long date) {
        MediaItem item = new MediaItem();
        item.date = date;
        return item;
    }

    public boolean isVideo() {
        return type == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO ;
    }
}
