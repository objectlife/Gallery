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

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.weshare.compose.R;
import com.weshare.domain.StickerItem;

import java.util.LinkedList;
import java.util.List;


public class StickerAdapter extends RecyclerView.Adapter<StickerAdapter.StickerHolder> {

    private final List<StickerItem> mStickers = new LinkedList<>();

    public StickerAdapter() {
        // 第一项为 文字贴图项, 默认添加到列表中
        mStickers.add(new StickerItem(StickerItem.CREATE_TEXT)) ;
    }

    @Override
    public StickerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sticker_item_layout,
                parent, false);
        return new StickerHolder(view);
    }


    @Override
    public void onBindViewHolder(StickerHolder holder, final int position) {
        final StickerItem stickerItem = getItem(position) ;
        if ( stickerItem.isRemoteSticker() ) {
            ImageLoader.getInstance().displayImage(stickerItem.resUrl, holder.thumbImage);
        } else if ( stickerItem.isCreateTextSticker() ){
            holder.thumbImage.setImageResource(R.drawable.ic_camera);
        } else {
            holder.thumbImage.setImageResource(stickerItem.resId);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(null, v, position, position);
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return mStickers.size();
    }


    private AdapterView.OnItemClickListener mOnItemClickListener;


    public void setOnItemClickListener(AdapterView.OnItemClickListener onFilterChangeListener) {
        this.mOnItemClickListener = onFilterChangeListener;
    }


    public StickerItem getItem(int position) {
        return mStickers.get(position);
    }

    public void addItem(StickerItem item) {
        if (item != null) {
            mStickers.add(item);
            notifyDataSetChanged();
        }
    }

    public void addItems(List<StickerItem> items) {
        if (items != null) {
            mStickers.addAll(items);
            notifyDataSetChanged();
        }
    }


    /**
     *
     */
    static class StickerHolder extends RecyclerView.ViewHolder {
        ImageView thumbImage;

        public StickerHolder(View itemView) {
            super(itemView);
            this.thumbImage = itemView.findViewById(R.id.item_imageview);
        }
    }
}
