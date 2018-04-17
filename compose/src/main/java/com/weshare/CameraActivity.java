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
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.cameraview.CameraView;
import com.google.android.cameraview.CameraViewImpl;
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
import com.weshare.compose.R;
import com.weshare.domain.MediaItem;
import com.weshare.tasks.SaveTask;

import java.io.File;
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
    private RecyclerView mGalleryRecyclerView;
    private GalleryAdapter mGalleryAdapter;


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
        initActionLayout();

        mProgressBar = findViewById(R.id.camera_progress_bar) ;

        findViewById(R.id.swipe_up_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(CameraActivity.this, "swipe up", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void initCameraView() {
        cameraView = findViewById(R.id.camera_view);
        shutterEffect = findViewById(R.id.shutter_effect);
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

    private void initImageLoaders() {

        DisplayImageOptions options = new DisplayImageOptions.Builder().bitmapConfig(Bitmap.Config.RGB_565).cacheInMemory(true).cacheOnDisk(true).build() ;

        // DON'T COPY THIS CODE TO YOUR PROJECT! This is just example of ALL options using.
        // See the sample project how to use ImageLoader correctly.
        File cacheDir = StorageUtils.getCacheDirectory(getApplicationContext());
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .memoryCacheExtraOptions(480, 800) // default = device screen dimensions
                .diskCacheExtraOptions(480, 800, null)
                .threadPoolSize(3) // default
                .threadPriority(Thread.NORM_PRIORITY - 2)       // default
                .tasksProcessingOrder(QueueProcessingType.LIFO) // default
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                .memoryCacheSize(2 * 1024 * 1024)
                .memoryCacheSizePercentage(13) // default
                .diskCache(new UnlimitedDiskCache(cacheDir)) // default
                .diskCacheSize(50 * 1024 * 1024)
                .diskCacheFileCount(100)
                .diskCacheFileNameGenerator(new HashCodeFileNameGenerator()) // default
                .imageDownloader(new BaseImageDownloader(getApplicationContext())) // default
                .defaultDisplayImageOptions(options) // default
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
        mGalleryRecyclerView = findViewById(R.id.effect_recyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mGalleryRecyclerView.setLayoutManager(layoutManager);

        mGalleryAdapter = new GalleryAdapter() ;
        mGalleryRecyclerView.setAdapter(mGalleryAdapter);
        mGalleryAdapter.setOnFilterChangeListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(CameraActivity.this, "click : " + mGalleryAdapter.getItem(position).path, Toast.LENGTH_SHORT).show();
            }
        });
        // init loader
        getSupportLoaderManager().initLoader(1, null, this) ;
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

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor cursor) {
        List<MediaItem> mediaItems = new LinkedList<>() ;
        while (cursor.moveToNext()) {
            long id = cursor.getLong(0) ;
            String filePath = cursor.getString(1) ;
            int mediaType = cursor.getInt(3) ;

            Log.e("ryze_photo", "### 1 media id : " + id
                    + ", 2 : " + filePath + ", 3 : " + cursor.getString(2) + ", 4 : " + mediaType);
            mediaItems.add(MediaItem.create(id, mediaType, filePath)) ;
        }
        mGalleryAdapter.addMedia(mediaItems);
        cursor.close();
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


//    private void addSticker(int position) {
//        Bitmap stickerBitmap = null;
//        switch (position) {
//            case 0:
//                stickerBitmap = BitmapFactory.decodeResource(getResources(),
//                        R.drawable.pink_flower);
//                break;
//            case 1:
//                stickerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.messenger);
//                break;
//            case 2:
//                stickerBitmap = BitmapFactory.decodeResource(getResources(),
//                        R.drawable.blue_flower);
//                break;
//        }
//
//        if (stickerBitmap != null) {
//            stickerView.addSticker(new DrawableSticker(new BitmapDrawable(stickerBitmap)));
//        }
//    }
//
//
//    private void showFilters() {
//        ObjectAnimator animator = ObjectAnimator.ofFloat(mEffectLayout, "translationY",
//                mEffectLayout.getHeight(), 0);
//        animator.setDuration(200);
//        animator.addListener(new AnimatorListenerAdapter() {
//
//            @Override
//            public void onAnimationStart(Animator animation) {
//                captureButton.setClickable(false);
//                mEffectLayout.setVisibility(View.VISIBLE);
//            }
//        });
//        animator.start();
//    }

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

    /**
     * 将拍照得到的照片添加到 sticker view 中的 ImageView中，然后再对整个 stickerView 进行截图，得到跟贴纸合并后的图像, 最后保存
     * @param bitmap
     */
    private void savePictureAsync(final Bitmap bitmap) {
//        // display picture in the ImageView which in the StickerView
        //        prevImageView.setImageBitmap(bitmap);
        //        prevImageView.setVisibility(View.VISIBLE);
        //
        //        // build the drawing cache of StickerView
        //        stickerView.setDrawingCacheEnabled(true);
        //        stickerView.buildDrawingCache();
        //
        //        // obtain drawing cache of StickerView, the drawing cache has the picture
        //        // and the stickers
        //        final Bitmap combineBitmap = stickerView.getDrawingCache();
        //
        //        // clear the ImageView
        //        prevImageView.setImageBitmap(null);
        //        prevImageView.setVisibility(View.GONE);

        // save picture
        new SaveTask(this, bitmap).start();
    }


    public void onPictureTaken(String filePath) {
        mProgressBar.setVisibility(View.GONE);
        Intent result = new Intent() ;
        result.setData(Uri.fromFile(new File(filePath)));
        setResult(RESULT_OK, result);
        finish();
    }


    public void destroyStickerDrawingCache() {
//        stickerView.destroyDrawingCache();
//        stickerView.setDrawingCacheEnabled(false);
    }
}
