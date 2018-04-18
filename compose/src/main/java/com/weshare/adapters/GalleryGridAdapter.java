package com.weshare.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.weshare.compose.R;
import com.weshare.domain.MediaItem;
import com.weshare.utils.DisplayUtil;

/**
 * Created by objectlife on 2018/4/17.
 */

public class GalleryGridAdapter extends GalleryAdapter {

    private static final int NUM_COLUMNS = 3;
    private int mImageViewSize = -1;

    @Override
    public int getItemViewType(int position) {
        return getItem(position).type;
    }

    @Override
    public void onBindViewHolder(GalleryHolder holder, int position) {
        final MediaItem item = getItem(position);
        if (item.type == MediaItem.TYPE_ITEM) {
            super.onBindViewHolder(holder, position);
            if (holder != null) {
                if (mImageViewSize == -1) {
                    Context context = holder.imageView.getContext();
                    int screenWidth = DisplayUtil.getDisplayWidthPixels(context);
                    mImageViewSize = (screenWidth - DisplayUtil.dip2px(context, 15)) / NUM_COLUMNS;
                }
                ViewGroup.LayoutParams params = holder.imageView.getLayoutParams();
                params.width = mImageViewSize;
                params.height = mImageViewSize;
                holder.imageView.setLayoutParams(params);
            }
        } else if (item.type == MediaItem.TYPE_HEADER) {
            holder.textView.setText(String.valueOf(item.headerDate));
        }
    }

    @Override
    public GalleryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == MediaItem.TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_img_header_item, parent, false);
            return new GalleryHolder(view);
        }
        return super.onCreateViewHolder(parent, viewType);
    }

}
