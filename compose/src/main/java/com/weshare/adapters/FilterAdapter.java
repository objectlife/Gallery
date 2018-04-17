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

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.weshare.compose.R;


public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.FilterHolder> {

    private String[] filters;
    private Context context;
    private int selected = 0;

    public FilterAdapter(Context context, String[] filters) {
        this.filters = filters;
        this.context = context;
    }


    @Override
    public FilterHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.sticker_item_layout,
                parent, false);
        return new FilterHolder(view);
    }


    @Override
    public void onBindViewHolder(FilterHolder holder, final int position) {
        switch (position){
            case 0:
                holder.thumbImage.setImageResource(R.drawable.pink_flower);
                break;
            case 1:
                holder.thumbImage.setImageResource(R.drawable.messenger);
                break;
            case 2:
                holder.thumbImage.setImageResource(R.drawable.blue_flower);
                break;
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (onFilterChangeListener != null) {
                    onFilterChangeListener.onFilterChanged(filters[position], position);
                }
                if (selected == position) {
                    return;
                }
                int lastSelected = selected;
                selected = position;
                notifyItemChanged(lastSelected);
                notifyItemChanged(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return filters == null ? 0 : filters.length;
    }


    private onFilterChangeListener onFilterChangeListener;

    public void setOnFilterChangeListener(onFilterChangeListener onFilterChangeListener) {
        this.onFilterChangeListener = onFilterChangeListener;
    }


    /**
     *
     */
    static class FilterHolder extends RecyclerView.ViewHolder {
        ImageView thumbImage;

        public FilterHolder(View itemView) {
            super(itemView);
            this.thumbImage = itemView.findViewById(R.id.item_imageview);
        }
    }

    /**
     *
     */
    public interface onFilterChangeListener {
        void onFilterChanged(String filterType, int position);
    }
}
