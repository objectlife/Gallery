package com.weshare.search;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.weshare.compose.R;
import com.weshare.domain.Category;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by mrsimple on 16/4/2018.
 */
public class SearchTagAdapter extends RecyclerView.Adapter<SearchTagAdapter.SearchTagVH> {

    private final List<Category> mCategories = new LinkedList<>() ;
    AdapterView.OnItemClickListener mItemClickListener;

    @Override
    public SearchTagVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_tag_item_layout, parent, false);
        return new SearchTagVH(itemView) ;
    }


    @Override
    public void onBindViewHolder(SearchTagVH holder, final int position) {
        Category category = mCategories.get(position) ;
        holder.categoryTv.setText(category.name);
        if ( category != null && category.tagsList.size() > 0 ) {
            holder.categoryInfoTv.setText(category.tagsList.get(0).name);
        }
        holder.iconImageView.setImageResource(R.drawable.ic_camera);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClick(null, v, position, position);
                }
            }
        });
    }

    public Category getItem(int position) {
        return mCategories.get(position) ;
    }

    public void clear() {
        mCategories.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mCategories.size();
    }


    public void addCategory(Category category) {
        mCategories.add(category) ;
    }


    public void setOnItemClickListener(AdapterView.OnItemClickListener itemClickListener) {
        this.mItemClickListener = itemClickListener;
    }

    /**
     *
     */
    static class SearchTagVH extends RecyclerView.ViewHolder {
        ImageView iconImageView ;
        TextView categoryTv ;
        TextView categoryInfoTv ;

        public SearchTagVH(View itemView) {
            super(itemView);
            iconImageView = itemView.findViewById(R.id.tag_icon_imageview) ;
            categoryTv = itemView.findViewById(R.id.tag_name_tv) ;
            categoryInfoTv = itemView.findViewById(R.id.tag_info_tv) ;
        }
    }
}
