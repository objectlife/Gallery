package com.weshare.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;
import com.weshare.compose.R;
import com.weshare.utils.DisplayUtil;

/**
 * Created by objectlife on 2018/4/17.
 */

public class GalleryHeaderAdapter extends GalleryAdapter implements StickyRecyclerHeadersAdapter<RecyclerView.ViewHolder> {

    private int mImageViewSize = -1;

    @Override
    public void onBindViewHolder(GalleryHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        if (holder != null) {
            if (mImageViewSize == -1) {
                Context context = holder.imageView.getContext();
                int screenWidth = DisplayUtil.getDisplayWidthPixels(context);
                mImageViewSize = (screenWidth - DisplayUtil.dip2px(context, 15)) / 3;
            }
            ViewGroup.LayoutParams params = holder.imageView.getLayoutParams();
            params.width = mImageViewSize;
            params.height = mImageViewSize;
            holder.imageView.setLayoutParams(params);
        }
    }

    @Override
    public long getHeaderId(int position) {
        return 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_img_header_item, parent, false);
        return new RecyclerView.ViewHolder(view) {
        };
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {
        TextView textView = (TextView) holder.itemView;
        textView.setText(String.valueOf(getItem(position).id));
    }
}
