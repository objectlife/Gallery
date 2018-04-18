package com.weshare;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.theartofdev.edmodo.cropper.CropImage;
import com.weshare.adapters.StickerAdapter;
import com.weshare.compose.R;
import com.weshare.domain.StickerItem;
import com.weshare.utils.DisplayUtil;
import com.weshare.widgets.SmartStickerView;
import com.xiaopo.flying.sticker.DrawableSticker;

import java.io.File;

/**
 * Created by mrsimple on 17/4/2018.
 */

public class ImageEditActivity extends AppCompatActivity {

    private static Bitmap sBitmap ;
    RecyclerView mStickerRecyclerView ;
    StickerAdapter mAdapter ;
    ImageView mImageView ;

    public static void start(Context context, String filePath) {
        Intent starter = new Intent(context, ImageEditActivity.class);
        starter.putExtra("file", filePath);
        context.startActivity(starter);
    }

    public static void setPrevBitmap(Bitmap bitmap) {
        ImageEditActivity.sBitmap = bitmap ;
    }

    String filePath ;
    SmartStickerView mStickerLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_edit_layout);

        mStickerRecyclerView = findViewById(R.id.sticker_recyclerView) ;
        mAdapter = new StickerAdapter() ;
        LinearLayoutManager layoutManager = new LinearLayoutManager(this) ;
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mStickerRecyclerView.setLayoutManager(layoutManager);
        mStickerRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                clickSticker(position);
            }
        });

        mockStickers();

        mImageView = findViewById(R.id.prev_imageview) ;

        mStickerLayout = findViewById(R.id.sticker_layout) ;
        ViewGroup.LayoutParams params = mStickerLayout.getLayoutParams();
        if (params != null) {
            params.height = DisplayUtil.getDisplayheightPixels(this) - DisplayUtil.dip2px(this, 200);
            mStickerLayout.setLayoutParams(params);
        }

        filePath = getIntent().getStringExtra("file") ;
        if ( sBitmap != null ){
            mImageView.setImageBitmap(sBitmap);
        } else if ( !TextUtils.isEmpty(filePath) ) {
            ImageLoader.getInstance().displayImage("file://" + filePath, mImageView);
        } else {
            Toast.makeText(this, R.string.no_images, Toast.LENGTH_SHORT).show();
            finish();
        }


        findViewById(R.id.toolbar_left_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.toolbar_crop_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                d.a(b).a(0.0f).a((int) R.drawable.ic_tick_white_24dp).a(this.mActivity);
                // 拿到图片之后先裁剪.
                CropImage.activity(Uri.fromFile(new File(filePath)))
                        .setCropMenuCropButtonIcon(R.drawable.ic_check_box_green_24dp)
//                        .setScaleType(CropImageView.ScaleType.CENTER_INSIDE)
                        .setAutoZoomEnabled(false)
                        .setInitialCropWindowPaddingRatio(0.0f)
                        .start(ImageEditActivity.this);
            }
        });
    }


    private void clickSticker(int position) {
        if ( position == 0 ) {
            createTextSticker() ;
        } else {
            createImageSticker(position);
        }
    }

    private void createTextSticker() {
        Toast.makeText(this, "create text sticker", Toast.LENGTH_SHORT).show();
    }


    private void createImageSticker(int position) {
        StickerItem stickerItem = mAdapter.getItem(position) ;
        Bitmap stickerBitmap = null;
        if ( stickerItem.isRemoteSticker() ) {
            Toast.makeText(this, "从缓存中读取bitmap sticker", Toast.LENGTH_SHORT).show();
        } else {
            stickerBitmap = BitmapFactory.decodeResource(getResources(), stickerItem.resId ) ;
        }

        if (stickerBitmap != null) {
            mStickerLayout.addSticker(new DrawableSticker(new BitmapDrawable(stickerBitmap)));
        }
    }


    private void mockStickers() {
        for (int i = 0; i < 10; i++) {
            if ( i % 3 == 0 ) {
                mAdapter.addItem(new StickerItem("https://avatar.csdn.net/3/5/2/1_bboyfeiyu.jpg?1523963219"));
            } else {
                mAdapter.addItem(new StickerItem(R.drawable.blue_flower));
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        sBitmap = null ;
    }
}
