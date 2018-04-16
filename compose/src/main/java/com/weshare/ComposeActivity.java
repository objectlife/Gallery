package com.weshare;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewStub;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.weshare.compose.R;
import com.weshare.utils.DisplayUtil;
import com.weshare.utils.MediaUtils;

/**
 * TODO : 底部栏样式先不管.  后续可以不设置windowSoftInputMode， 底部栏通过Dialog 来实现.
 *
 * Created by mrsimple on 12/4/2018.
 */

public class ComposeActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int SELECT_IMAGE = 7723;
    private static final int SELECT_CAMERA = 7724;
    private static final int SELECT_VIDEO = 7725;

    View mMediaSelectLayout ;
    ViewStub mPrevStub;
    ImageView mPrevImageView;
    ImageView mPrevTypeImageView;
    View mPrevLayout ;
    EditText mPostEdit ;
    Uri mMediaUri;


    public static void start(Context context) {
        Intent starter = new Intent(context, ComposeActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.compose_activity);

        Toolbar toolbar = findViewById(R.id.id_toolbar);
        //继承自ActionBarActivity
        setSupportActionBar(toolbar);
        //隐藏Toolbar的标题
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        findViewById(R.id.toolbar_left_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.select_img_button).setOnClickListener(this);
        findViewById(R.id.select_video_button).setOnClickListener(this);
        findViewById(R.id.take_camera_button).setOnClickListener(this);

        mMediaSelectLayout = findViewById(R.id.media_select_layout) ;

        mPostEdit = findViewById(R.id.edit_text) ;
        findViewById(R.id.toolbar_right_button).setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.select_img_button) {
            performButtonClick(SELECT_IMAGE);
        }

        if (v.getId() == R.id.select_video_button) {
            performButtonClick(SELECT_VIDEO);
        }

        if (v.getId() == R.id.take_camera_button) {
            performButtonClick(SELECT_CAMERA);
        }

        if (v.getId() == R.id.toolbar_right_button) {
            ChooseCategoryActivity.start(this, mMediaUri, mPostEdit.getText().toString().trim());
        }
    }


    private void performButtonClick(int reqCode) {
        Intent intent = null;

        if (reqCode == SELECT_IMAGE) {
            intent = new Intent("android.intent.action.PICK", MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
        }

        if (reqCode == SELECT_VIDEO) {
            intent = new Intent("android.intent.action.PICK", MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
            intent.setType("video/*");
        }

        if (reqCode == SELECT_CAMERA) {
            intent = new Intent(this, CameraActivity.class) ;
        }

        if (intent != null) {
            startActivityForResult(intent, reqCode);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                showImagePreview(result.getUri());
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show();
                closePreviewLayout();
            } else {
                closePreviewLayout();
            }
            showKeyboard();
        } else if ( resultCode == Activity.RESULT_OK ){
            handleActivityResult(requestCode, data);
        } else {
            Toast.makeText(this, "obtain media result : " + resultCode, Toast.LENGTH_SHORT).show();
            closePreviewLayout();
        }
    }


    private void handleActivityResult(int requestCode, final Intent data) {
        if (mPrevStub == null) {
            mPrevStub = findViewById(R.id.media_preview_viewstub);
            mPrevLayout = mPrevStub.inflate();
            mPrevImageView = mPrevLayout.findViewById(R.id.media_prev_img);
            mPrevTypeImageView = mPrevLayout.findViewById(R.id.media_prev_type_img);

            // hide prev layout & show keyboard
            mPrevLayout.findViewById(R.id.close_image_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    closePreviewLayout();
                }
            });
        }

        if ( mPrevImageView != null && data != null ) {
            if ( requestCode == SELECT_IMAGE ) {
                // 拿到图片之后先裁剪.
                CropImage.activity(data.getData())
                        .setInitialCropWindowRectangle(new Rect(0,0, DisplayUtil.getDisplayWidthPixels(this), DisplayUtil.getDisplayWidthPixels(this)))
                        .setCropShape(CropImageView.CropShape.RECTANGLE)
                        .setCropMenuCropButtonIcon(R.drawable.ic_check_box_green_24dp)
                        .start(this);
            } else if (requestCode == SELECT_VIDEO ) {
                new Thread() {
                    @Override
                    public void run() {
                        showVideoPreview(data);
                    }
                }.start();
            } else if ( requestCode == SELECT_CAMERA ) {
                showImagePreview(data.getData());
            }
        }

        mMediaSelectLayout.setVisibility(View.GONE);
    }


    private void closePreviewLayout() {
        if ( mPrevTypeImageView != null ) {
            mPrevTypeImageView.setVisibility(View.GONE);
        }
        if ( mPrevLayout != null ) {
            mPrevLayout.setVisibility(View.GONE);
        }

        mMediaSelectLayout.setVisibility(View.VISIBLE);
        showKeyboard();
        mMediaUri = null;
    }


    private void showImagePreview(Uri uri) {
        mPrevTypeImageView.setVisibility(View.GONE);
        mPrevLayout.setVisibility(View.VISIBLE);
        mMediaSelectLayout.setVisibility(View.GONE);
        Picasso.with(this).load(uri).into(mPrevImageView);
        mMediaUri = uri ;
    }


    private void showKeyboard() {
        mPostEdit.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPostEdit.requestFocus() ;
                InputMethodManager imm = (InputMethodManager)   getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(mPostEdit, InputMethodManager.SHOW_IMPLICIT) ;
            }
        }, 300);
    }


    private void showVideoPreview(final Intent data) {
        mMediaUri = data.getData() ;
        final Bitmap thumb = MediaUtils.getVideoThumb(ComposeActivity.this.getApplicationContext(), mMediaUri) ;
        mPrevImageView.post(new Runnable() {
            @Override
            public void run() {
                mPrevImageView.setImageBitmap(thumb);
                mPrevTypeImageView.setImageResource(R.drawable.ic_compose_video);
                mPrevTypeImageView.setVisibility(View.VISIBLE);
            }
        });
    }
}
