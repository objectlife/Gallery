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

package com.weshare.adapters;

/**
 * Created by mrsimple on 14/4/2018.
 */

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.weshare.compose.R;
import com.weshare.domain.MediaItem;

import java.util.LinkedList;
import java.util.List;


public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.GalleryHolder> {

    private final List<MediaItem> mediaItems = new LinkedList<>();
    private AdapterView.OnItemClickListener mOnItemClickListener;


    @Override
    public GalleryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sticker_item_layout, parent, false);
        return new GalleryHolder(view);
    }

    final DisplayImageOptions options = new DisplayImageOptions.Builder().bitmapConfig(Bitmap.Config.RGB_565).cacheInMemory
            (true).cacheOnDisk(false).showImageOnLoading(R.drawable.image_loading).showImageOnFail(R.drawable
            .category_item_bg).build();

    @Override
    public void onBindViewHolder(GalleryHolder holder, final int position) {
        final MediaItem item = mediaItems.get(position);
        if (item.isVideo()) {
            holder.typeImageView.setVisibility(View.VISIBLE);
        } else {
            holder.typeImageView.setVisibility(View.GONE);
        }
        ImageLoader.getInstance().displayImage("file://" + item.path, holder.imageView, options);

        holder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(null, v, position, position);
                }
            }
        });
    }


    public MediaItem getItem(int position) {
        return mediaItems.get(position);
    }

    public void addMedia(MediaItem item) {
        if (item != null) {
            mediaItems.add(item);
            notifyDataSetChanged();
        }
    }

    public void addMedia(List<MediaItem> items) {
        if (items != null) {
            mediaItems.addAll(items);
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        return mediaItems.size();
    }


    public void setOnFilterChangeListener(AdapterView.OnItemClickListener onFilterChangeListener) {
        this.mOnItemClickListener = onFilterChangeListener;
    }

    /**
     *
     */
    static class GalleryHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        ImageView typeImageView;
        TextView textView;

        public GalleryHolder(View itemView) {
            super(itemView);
            this.imageView = itemView.findViewById(R.id.item_imageview);
            this.typeImageView = itemView.findViewById(R.id.item_type_imageview);
            this.textView = itemView.findViewById(R.id.tv_img_date);
        }
    }
}
