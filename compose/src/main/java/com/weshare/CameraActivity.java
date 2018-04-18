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

package com.weshare;


import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.cameraview.CameraView;
import com.google.android.cameraview.CameraViewImpl;
import com.google.android.cameraview.FocusView;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.weshare.adapters.GalleryAdapter;
import com.weshare.adapters.GalleryGridAdapter;
import com.weshare.compose.R;
import com.weshare.domain.MediaItem;
import com.weshare.tasks.SaveTask;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

/**
 * Camera Activity
 */
public class CameraActivity extends AppCompatActivity implements View.OnClickListener , LoaderManager.LoaderCallbacks<Cursor> {

    private static final int PERMISSION_CODE_STORAGE = 3001;
    private static final int PERMISSION_CODE_CAMERA = 3002;

    CameraView cameraView;
    View shutterEffect;
    View captureButton;
    View switchButton;
    View actionLayout;
    private ImageView flashButton;
    private boolean isTakingPhoto = false;
    ProgressBar mProgressBar ;
    private RecyclerView mGalleryLinearView;
    private GalleryAdapter mGalleryLinearAdapter;

    private BottomSheetBehavior mBehavior;

    private View mGalleryGridLayout;
    private RecyclerView mGalleryGridView;
    private GalleryGridAdapter mGalleryGridAdapter;

    View mStickerRootLayout ;
    View mBottomSheetLayout ;

    boolean isAnimating = false ;

    public static void start(Context context) {
        Intent starter = new Intent(context, CameraActivity.class);
        context.startActivity(starter);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        initCameraView();
        initGalleryRecyclerView();
        initGalleryContentView();
        initActionLayout();

        mProgressBar = findViewById(R.id.camera_progress_bar) ;

        findViewById(R.id.swipe_up_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(CameraActivity.this, "swipe up", Toast.LENGTH_SHORT).show();
            }
        });
        initBehavior();
    }


    private void initCameraView() {
        cameraView = findViewById(R.id.camera_view);
        cameraView.setFlash(CameraView.FLASH_AUTO);
        FocusView view = findViewById(R.id.focus_view) ;
        cameraView.setFocusView(view);
        cameraView.setOnTapListener(new CameraView.OnTapListener() {
            @Override
            public void onTap() {
                hideBottomSheetLayout();
            }
        });

        shutterEffect = findViewById(R.id.shutter_effect);
    }



    private void hideBottomSheetLayout() {
        if ( mBottomSheetLayout != null && !isAnimating && mGalleryLinearAdapter.getItemCount() > 0 ) {
            isAnimating = true ;
            mBottomSheetLayout.animate().translationX(-getResources().getDisplayMetrics().widthPixels).setInterpolator(new DecelerateInterpolator()).setListener(new AnimatorListenerAdapter() {

                @Override
                public void onAnimationEnd(Animator animation) {
                    isAnimating = false ;
                    mBottomSheetLayout.setVisibility(View.GONE);
                }
            }).setDuration(500).start();
        }
    }




    private void initActionLayout() {
        actionLayout = findViewById(R.id.bottom_layout);

        captureButton = findViewById(R.id.take_photo_btn);
        captureButton.setOnClickListener(this);

        switchButton = findViewById(R.id.switch_camera_btn);
        switchButton.setOnClickListener(this);

        flashButton = findViewById(R.id.flash_btn);
        flashButton.setOnClickListener(this);

        initImageLoaders();
    }


    private void initBehavior() {
        mBottomSheetLayout = findViewById(R.id.design_bottom_sheet) ;
        mBehavior = BottomSheetBehavior.from(mBottomSheetLayout);
        mBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        mGalleryGridLayout.setVisibility(View.GONE);

                        mStickerRootLayout.setAlpha(1.0f);
                        actionLayout.setAlpha(1.0f);
                        break;

                    case BottomSheetBehavior.STATE_EXPANDED:
                        mGalleryGridLayout.setVisibility(View.VISIBLE);
                        mStickerRootLayout.setAlpha(0.0f);
                        actionLayout.setAlpha(0.0f);
                        break;

                    case BottomSheetBehavior.STATE_DRAGGING:
                        mGalleryGridLayout.setVisibility(View.VISIBLE);
                        // todo :
                        if ( mGalleryGridAdapter.getItemCount() <= 0 ) {
                            mGalleryGridAdapter.addItems(mContentMediaItems);
                        }
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                float alpha =  Math.max(0, 1 - (slideOffset + 0.5f));
                mStickerRootLayout.setAlpha(alpha);
                actionLayout.setAlpha(alpha);
                mGalleryGridLayout.setAlpha(slideOffset);
            }
        });
    }

    private void initImageLoaders() {

        DisplayImageOptions options = new DisplayImageOptions.Builder().bitmapConfig(Bitmap.Config.RGB_565).showImageOnLoading(R.drawable.image_loading).cacheInMemory(true).cacheOnDisk(true).build() ;

        // DON'T COPY THIS CODE TO YOUR PROJECT! This is just example of ALL options using.
        // See the sample project how to use ImageLoader correctly.
        File cacheDir = StorageUtils.getCacheDirectory(getApplicationContext());
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .threadPoolSize(3) // default
                .threadPriority(Thread.NORM_PRIORITY - 2)       // default
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                .memoryCacheSize(2 * 1024 * 1024)
                .memoryCacheSizePercentage(13)                  // default
                .diskCache(new UnlimitedDiskCache(cacheDir)) // default
                .diskCacheSize(50 * 1024 * 1024)
                .diskCacheFileCount(200)
                .diskCacheFileNameGenerator(new HashCodeFileNameGenerator())        // default
                .imageDownloader(new BaseImageDownloader(getApplicationContext())) // default
                .defaultDisplayImageOptions(options)                                // default
                .writeDebugLogs()
                .build();

        ImageLoader.getInstance().init(config);
    }


    @Override
    public void onClick(View v) {
        if ( isTakingPhoto ) {
            return;
        }
        final int viewId = v.getId() ;
        if ( viewId == R.id.take_photo_btn ) {
            isTakingPhoto = true;
            cameraView.takePicture();
        }

        if ( viewId == R.id.switch_camera_btn ) {
            cameraView.switchCamera();
        }

        if ( viewId == R.id.flash_btn) {
            switchFlashMode();
        }
    }

    private void initGalleryRecyclerView() {
        mStickerRootLayout = findViewById(R.id.sticker_root_layout) ;
        mStickerRootLayout.setTranslationX(-getResources().getDisplayMetrics().widthPixels);

        mGalleryLinearView = findViewById(R.id.effect_recyclerView);
        mGalleryLinearView.setNestedScrollingEnabled(false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mGalleryLinearView.setLayoutManager(layoutManager);

        mGalleryLinearAdapter = new GalleryAdapter() ;
        mGalleryLinearView.setAdapter(mGalleryLinearAdapter);
        mGalleryLinearAdapter.setOnFilterChangeListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(CameraActivity.this, "click : " + mGalleryLinearAdapter.getItem(position).path, Toast.LENGTH_SHORT).show();
                ImageEditActivity.start(CameraActivity.this, mGalleryLinearAdapter.getItem(position).path);
            }
        });
        // init loader
        getSupportLoaderManager().initLoader(1, null, this) ;
    }

    private void initGalleryContentView() {
        mGalleryGridLayout = findViewById(R.id.gallery_content);
        mGalleryGridView = findViewById(R.id.gallery_view);
//        mGalleryGridView.setNestedScrollingEnabled(false);
        GridLayoutManager manager = new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false);
        mGalleryGridView.setLayoutManager(manager);
        mGalleryGridAdapter = new GalleryGridAdapter();
        mGalleryGridAdapter.setOnFilterChangeListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(CameraActivity.this, "click : " + mGalleryGridAdapter.getItem(position).path, Toast.LENGTH_SHORT).show();
            }
        });

        mGalleryGridView.setAdapter(mGalleryGridAdapter);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (mGalleryGridAdapter.getItemViewType(position)) {
                    case MediaItem.TYPE_HEADER:
                        return 3;
                    default:
                        return 1;
                }
            }
        });
    }


    // Get relevant columns for use later.
    String[] projection = {
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.DATA,
            MediaStore.Files.FileColumns.DATE_ADDED,
            MediaStore.Files.FileColumns.MEDIA_TYPE,
            MediaStore.Files.FileColumns.MIME_TYPE,
            MediaStore.Files.FileColumns.TITLE
    };

    // Return only video and image metadata.
    String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "="
            + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
            + " OR "
            + MediaStore.Files.FileColumns.MEDIA_TYPE + "="
            + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;

    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri queryUri = MediaStore.Files.getContentUri("external");
        return new CursorLoader(this, queryUri, projection, selection, null, // Selection args (none).
                MediaStore.Files.FileColumns.DATE_ADDED + " DESC" // Sort order.
        );
    }


    Handler mHandler = new Handler(Looper.getMainLooper()) ;
    final List<MediaItem> mContentMediaItems = new LinkedList<>() ;

    public static final long DAY_SECONDS = 24 * 60 * 60 ;
    MediaItem headerItem ;
    MediaItem othersHeaderItem;

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor cursor) {
        final List<MediaItem> mediaItems = new LinkedList<>() ;
        long nowTimestamp = System.currentTimeMillis() / 1000 ;
        try {
            while (cursor.moveToNext()) {
                long date = cursor.getLong(2);
                long offset = nowTimestamp - date ;
                if ( offset  <= 3 * DAY_SECONDS && headerItem == null ) {
                    headerItem = MediaItem.createHeaderItem("最近");
                    headerItem.type = MediaItem.TYPE_HEADER;
                    mContentMediaItems.add(headerItem);
                } else if ( offset  > 3 * DAY_SECONDS && othersHeaderItem == null ){
                    othersHeaderItem = MediaItem.createHeaderItem("3天之前");
                    othersHeaderItem.type = MediaItem.TYPE_HEADER;
                    mContentMediaItems.add(othersHeaderItem);
                }

                long id = cursor.getLong(0) ;
                String filePath = cursor.getString(1) ;
                int mediaType = cursor.getInt(3) ;
                MediaItem item = MediaItem.create(id, mediaType, filePath);
                item.type = MediaItem.TYPE_ITEM;
                Log.e("ryze_photo", "### 1 media id : " + id
                        + ", 2 : " + filePath + ", 3 : " + cursor.getString(2) + ", 4 : " + mediaType);
                mediaItems.add(item) ;
                mContentMediaItems.add(item);
            }
        } finally {
            if ( mediaItems.size() > 0 ) {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mStickerRootLayout.setVisibility(View.VISIBLE);
                        mStickerRootLayout.animate().translationX(0).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(600).start();
                        mGalleryLinearAdapter.addItems(mediaItems);
                    }
                }, 1000);
            }
            if ( cursor != null ) {
                cursor.close();
            }
        }
    }


    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {

    }


    @Override
    protected void onResume() {
        super.onResume();
        if (PermissionUtils.isStorageGranted(this) && PermissionUtils.isCameraGranted(this)) {
            cameraView.start();
            setupCameraCallbacks();
        } else {
            if (!PermissionUtils.isCameraGranted(this)) {
                PermissionUtils.checkPermission(this, Manifest.permission.CAMERA,
                        PERMISSION_CODE_CAMERA);
            } else {
                PermissionUtils.checkPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        PERMISSION_CODE_STORAGE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_CODE_STORAGE:
            case PERMISSION_CODE_CAMERA:
                if (!(grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
        }
        if (requestCode != PERMISSION_CODE_STORAGE && requestCode != PERMISSION_CODE_CAMERA) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onPause() {
        cameraView.stop();
        super.onPause();
    }

    private void setupCameraCallbacks() {
        cameraView.setOnPictureTakenListener(new CameraViewImpl.OnPictureTakenListener() {
            @Override
            public void onPictureTaken(Bitmap bitmap) {
                isTakingPhoto = false;
                savePictureAsync(bitmap);
            }
        });
        cameraView.setOnFocusLockedListener(new CameraViewImpl.OnFocusLockedListener() {
            @Override
            public void onFocusLocked() {
                playShutterAnimation();
            }
        });
        cameraView.setOnTurnCameraFailListener(new CameraViewImpl.OnTurnCameraFailListener() {
            @Override
            public void onTurnCameraFail(Exception e) {
                Toast.makeText(CameraActivity.this,
                        "Switch Camera Failed. Does you device has a front camera?",
                        Toast.LENGTH_SHORT).show();
            }
        });
        cameraView.setOnCameraErrorListener(new CameraViewImpl.OnCameraErrorListener() {
            @Override
            public void onCameraError(Exception e) {
                isTakingPhoto = false;
                Toast.makeText(CameraActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void playShutterAnimation() {
        shutterEffect.setVisibility(View.VISIBLE);
        shutterEffect.animate().alpha(0f).setDuration(300).setListener(
                new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        shutterEffect.setVisibility(View.GONE);
                        shutterEffect.setAlpha(0.6f);

                        mProgressBar.setVisibility(View.VISIBLE);
                    }
                });
    }


    private void switchFlashMode() {
        int mode = cameraView.getFlash() ;
        int iconRes;
        switch ( mode ) {
            case CameraView.FLASH_OFF:
                mode = CameraView.FLASH_ON ;
                iconRes = R.drawable.flash_on ;
                break;
            case CameraView.FLASH_ON:
                mode = CameraView.FLASH_AUTO ;
                iconRes = R.drawable.flash_auto ;
                break;

            case CameraView.FLASH_AUTO:
                mode = CameraView.FLASH_OFF ;
                iconRes = R.drawable.flash_off ;
                break;

            default :
                mode = CameraView.FLASH_OFF ;
                iconRes = R.drawable.flash_off ;
                break;
        }
        cameraView.setFlash(mode);
        flashButton.setImageResource(iconRes);
    }

    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyyMMddHHmmss");

    /**
     * 将拍照得到的照片添加到 sticker view 中的 ImageView中，然后再对整个 stickerView 进行截图，得到跟贴纸合并后的图像, 最后保存
     * @param bitmap
     */
    private void savePictureAsync(final Bitmap bitmap) {
        ImageEditActivity.setPrevBitmap(bitmap);
        String fileName = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + File.separator
                + getString(R.string.app_name) + SIMPLE_DATE_FORMAT.format(Calendar.getInstance().getTime()) + ".jpg" ;
        // 先跳转到编辑页面, 然后再保存, 避免保存太耗时导致等待时长偏长
        ImageEditActivity.start(this, fileName);

        mProgressBar.setVisibility(View.GONE);
        // save picture
        new SaveTask(this, bitmap, fileName).start();
    }


    public void onPictureTaken(String filePath) {
//        ImageEditActivity.start(this, filePath);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ( mGalleryGridLayout.isShown() && mBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED ) {
            mBehavior.setHideable(true);
            mBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onBackPressed() {
        if ( mGalleryGridLayout.isShown() && mBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED ) {
            mBehavior.setHideable(true);
            mBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }

}
