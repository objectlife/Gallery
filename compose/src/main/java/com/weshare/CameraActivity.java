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
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.cameraview.CameraView;
import com.google.android.cameraview.CameraViewImpl;
import com.weshare.compose.R;
import com.weshare.tasks.SaveTask;

import java.io.File;

/**
 * Camera Activity
 */
public class CameraActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int PERMISSION_CODE_STORAGE = 3001;
    private static final int PERMISSION_CODE_CAMERA = 3002;

    CameraView cameraView;

    View shutterEffect;
    View captureButton;
    View switchButton;
    com.xiaopo.flying.sticker.StickerView stickerView;
    ImageView prevImageView;
    View actionLayout;
    private ImageView flashButton;
    private boolean isTakingPhoto = false;
    ProgressBar mProgressBar ;


    public static void start(Context context) {
        Intent starter = new Intent(context, CameraActivity.class);
        context.startActivity(starter);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

//        cameraHeight = (int) (getResources().getDisplayMetrics().widthPixels * 4.0f / 3);

        initCameraView();
//        initStickerLayout();
        initActionLayout();

        mProgressBar = findViewById(R.id.camera_progress_bar) ;
    }


    private void initCameraView() {
        cameraView = findViewById(R.id.camera_view);
//        ViewGroup.LayoutParams params = cameraView.getLayoutParams();
//        if (params != null) {
//            params.width = getResources().getDisplayMetrics().widthPixels;
//            params.height = cameraHeight;
//            cameraView.setLayoutParams(params);
//        }

        shutterEffect = findViewById(R.id.shutter_effect);

//        ViewGroup.LayoutParams shutterEffectLayoutParams = shutterEffect.getLayoutParams();
//        if (shutterEffectLayoutParams != null) {
//            shutterEffectLayoutParams.width = getResources().getDisplayMetrics().widthPixels;
//            shutterEffectLayoutParams.height = cameraHeight;
//            shutterEffect.setLayoutParams(shutterEffectLayoutParams);
//        }
    }

//    private void initStickerLayout() {
//        stickerView = findViewById(R.id.sticker_view);
//        ViewGroup.LayoutParams params = stickerView.getLayoutParams();
//        if (params != null) {
//            params.height = cameraHeight;
//            stickerView.setLayoutParams(params);
//        }
//
//        prevImageView = findViewById(R.id.prev_imageview);
//        ViewGroup.LayoutParams prevParams = prevImageView.getLayoutParams();
//        if (prevParams != null) {
//            prevParams.height = cameraHeight;
//            prevImageView.setLayoutParams(prevParams);
//        }
//    }
//
//    private int getBottomActionLayoutHeight() {
//        return getResources().getDisplayMetrics().heightPixels - cameraHeight;
//    }


    private void initActionLayout() {
        actionLayout = findViewById(R.id.bottom_layout);
//        ViewGroup.LayoutParams params = actionLayout.getLayoutParams();
//        if (params != null) {
//            params.width = getResources().getDisplayMetrics().widthPixels;
//            params.height = getBottomActionLayoutHeight();
//            actionLayout.setLayoutParams(params);
//        }

        captureButton = findViewById(R.id.take_photo_btn);
        captureButton.setOnClickListener(this);

        switchButton = findViewById(R.id.switch_camera_btn);
        switchButton.setOnClickListener(this);

        flashButton = findViewById(R.id.flash_btn);
        flashButton.setOnClickListener(this);
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

//    private void initEffectRecyclerView() {
//        mEffectLayout = findViewById(R.id.stickers_layout);
//
//        ViewGroup.LayoutParams params = mEffectLayout.getLayoutParams();
//        if (params != null) {
//            params.height = getBottomActionLayoutHeight();
//            mEffectLayout.setLayoutParams(params);
//        }
//
//        mEffectRecyclerView = findViewById(R.id.effect_recyclerView);
//
//        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
//        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
//        mEffectRecyclerView.setLayoutManager(layoutManager);
//
//        mFilterAdapter = new FilterAdapter(this, new String[]{"flower", "smile", "glassed"});
//        mFilterAdapter.setOnFilterChangeListener(new FilterAdapter.onFilterChangeListener() {
//            @Override
//            public void onFilterChanged(String filterType, int position) {
//                addSticker(position);
//                hideFilters();
//            }
//        });
//
//        mEffectRecyclerView.setAdapter(mFilterAdapter);
//
//        findViewById(R.id.btn_camera_closefilter).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                hideFilters();
//            }
//        });
//    }



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


//    private void hideFilters() {
//        ObjectAnimator animator = ObjectAnimator.ofFloat(mEffectLayout, "translationY", 0,
//                mEffectLayout.getHeight());
//        animator.setDuration(200);
//        animator.addListener(new AnimatorListenerAdapter() {
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                hideEffectLayout();
//            }
//
//            @Override
//            public void onAnimationCancel(Animator animation) {
//                hideEffectLayout();
//            }
//
//            private void hideEffectLayout() {
//                mEffectLayout.setVisibility(View.INVISIBLE);
//                captureButton.setClickable(true);
//            }
//        });
//        animator.start();
//    }


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
