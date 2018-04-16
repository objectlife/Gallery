package com.weshare;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.weshare.compose.R;
import com.weshare.domain.Category;
import com.weshare.utils.DisplayUtil;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Created by mrsimple on 16/4/2018.
 */

public class ChooseCategoryActivity extends AppCompatActivity {

    ImageView mPrevImageView;
    TextView mContentTextView;
    RecyclerView mCategoryRecyclerView;
    CategoryAdapter mAdapter = new CategoryAdapter();

    public static void start(Context context, Uri mediaUri, String text) {
        Intent starter = new Intent(context, ChooseCategoryActivity.class);
        starter.putExtra("uri", mediaUri);
        starter.putExtra("text", text);
        context.startActivity(starter);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_category_layout);

        mPrevImageView = findViewById(R.id.category_prev_imageview);
        mContentTextView = findViewById(R.id.category_textview);
        mCategoryRecyclerView = findViewById(R.id.category_recyclerview);

        Uri mediaUri = getIntent().getParcelableExtra("uri");
        // todo : 要判断类别图片还是视频
        if (mediaUri != null) {
            Picasso.with(this).load(mediaUri).into(mPrevImageView);
        }

        mContentTextView.setText(getIntent().getStringExtra("text"));

        for (int i = 0; i < 10; i++) {
            Category category = new Category();
            category.id = "id-" + i;
            category.name = "Word - " + i;
            mAdapter.mCategories.add(category);
        }

        mCategoryRecyclerView.setAdapter(mAdapter);
        mCategoryRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        mAdapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TagDialogFragment fragment = TagDialogFragment.newInstance() ;
                fragment.show(getSupportFragmentManager(), TagDialogFragment.class.getSimpleName());
            }
        });
    }

    /**
     *
     */
    public static class TagDialogFragment extends DialogFragment {

        public static TagDialogFragment newInstance() {
            Bundle args = new Bundle();
            TagDialogFragment fragment = new TagDialogFragment();
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onStart() {
            super.onStart();
            Window window = getDialog().getWindow();
            WindowManager.LayoutParams params = window.getAttributes();
            params.gravity = Gravity.BOTTOM;
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            window.setAttributes(params);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle
                savedInstanceState) {
            getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
            final View view = inflater.inflate(R.layout.tag_dialog_layout, container, false);
            slideToUp(view, new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    initWidgets(view);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            return view;
        }

        private void initWidgets(View itemView) {
            itemView.findViewById(R.id.post_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext(), "click post button", Toast.LENGTH_SHORT).show();
                }
            });
        }

        private static void slideToUp(View view, Animation.AnimationListener listener) {
            Animation slide = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                    0.0f, Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
            slide.setDuration(300);
            slide.setFillAfter(true);
            slide.setFillEnabled(true);
            slide.setAnimationListener(listener);
            view.startAnimation(slide);
        }
    }


    /**
     *
     */
    static class CategoryAdapter extends RecyclerView.Adapter<CategoryVH> {

        static float[] sRadius = null;

        final List<Category> mCategories = new LinkedList<>();
        AdapterView.OnItemClickListener mItemClickListener;
        private Random random = new Random();

        @Override
        public CategoryVH onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item_layout, parent,
                    false);
            return new CategoryVH(itemView);
        }

        @Override
        public void onBindViewHolder(CategoryVH holder, final int position) {
            holder.nameTv.setText(mCategories.get(position).name);

            GradientDrawable shape = new GradientDrawable();
            shape.setShape(GradientDrawable.RECTANGLE);
            int radius = DisplayUtil.dip2px(holder.itemView.getContext(), 5);
            if (sRadius == null) {
                sRadius = new float[]{radius, radius, radius, radius, radius, radius, radius, radius};
            }
            shape.setCornerRadii(sRadius);
            shape.setColor(Color.argb(255, random.nextInt(200), random.nextInt(200), random.nextInt(200)));
            holder.itemView.setBackgroundDrawable(shape);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mItemClickListener != null) {
                        mItemClickListener.onItemClick(null, v, position, position);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mCategories.size();
        }

        public void setOnItemClickListener(AdapterView.OnItemClickListener itemClickListener) {
            this.mItemClickListener = itemClickListener;
        }
    }


    /**
     *
     */
    static class CategoryVH extends RecyclerView.ViewHolder {

        TextView nameTv;

        public CategoryVH(View itemView) {
            super(itemView);
            nameTv = itemView.findViewById(R.id.category_name_tv);
        }
    }
}
