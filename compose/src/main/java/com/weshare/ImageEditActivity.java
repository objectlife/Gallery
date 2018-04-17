package com.weshare;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.weshare.compose.R;
import com.weshare.utils.DisplayUtil;
import com.weshare.widgets.SmartStickerView;

import java.io.File;

/**
 * Created by mrsimple on 17/4/2018.
 */

public class ImageEditActivity extends AppCompatActivity {

    private static Bitmap sBitmap ;

    RecyclerView mStickerRecyclerView ;
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

    final DisplayImageOptions options = new DisplayImageOptions.Builder().bitmapConfig(Bitmap.Config.RGB_565).cacheInMemory
            (true).cacheOnDisk(false).showImageOnLoading(R.drawable.image_loading).showImageOnFail(R.drawable
            .category_item_bg).build();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_edit_layout);

        mStickerRecyclerView = findViewById(R.id.sticker_recyclerView) ;
        mImageView = findViewById(R.id.prev_imageview) ;

        mStickerLayout = findViewById(R.id.sticker_layout) ;
        ViewGroup.LayoutParams params = mStickerLayout.getLayoutParams();
        if (params != null) {
            params.height = DisplayUtil.getDisplayheightPixels(this) - DisplayUtil.dip2px(this, 160);
            mStickerLayout.setLayoutParams(params);
        }

        filePath = getIntent().getStringExtra("file") ;
        if ( sBitmap != null ){
            mImageView.setImageBitmap(sBitmap);
        } else if ( !TextUtils.isEmpty(filePath) ) {
            loadBitmap();
        } else {
            Toast.makeText(this, R.string.no_images, Toast.LENGTH_SHORT).show();
            finish();
        }

        findViewById(R.id.toolbar_crop_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 拿到图片之后先裁剪.
                CropImage.activity(Uri.fromFile(new File(filePath)))
                        .setCropShape(CropImageView.CropShape.RECTANGLE)
                        .setCropMenuCropButtonIcon(R.drawable.ic_check_box_green_24dp)
                        .start(ImageEditActivity.this);
            }
        });
    }


    private void loadBitmap() {
//        new Thread() {
//            @Override
//            public void run() {
//                final Bitmap bitmap = BitmapFactory.decodeFile(filePath) ;
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        mImageView.setImageBitmap(bitmap);
//                    }
//                });
//            }
//        }.start();

        ImageLoader.getInstance().displayImage("file://" + filePath, mImageView);
    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
        sBitmap = null ;
    }
}
