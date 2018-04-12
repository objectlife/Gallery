package com.hkh.funny.compose;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * TODO : 底部栏样式先不管.
 *
 * Created by mrsimple on 12/4/2018.
 */

public class ComposeActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int SELECT_AUDIO = 7726;
    public static final int SELECT_CAMERA = 7727;
    public static final int SELECT_GIF = 7724;
    public static final int SELECT_IMAGE = 7723;
    public static final int SELECT_VIDEO = 7725;


    public static void start(Context context) {
        Intent starter = new Intent(context, ComposeActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.compose_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.id_toolbar);
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
        findViewById(R.id.take_gif_button).setOnClickListener(this);
        findViewById(R.id.take_music_button).setOnClickListener(this);
        findViewById(R.id.take_camera_button).setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.select_img_button) {
            performButtonClick(SELECT_IMAGE);
        }

        if (v.getId() == R.id.select_video_button) {
            performButtonClick(SELECT_VIDEO);
        }

        if (v.getId() == R.id.take_music_button) {
            performButtonClick(SELECT_AUDIO);
        }

        if (v.getId() == R.id.take_gif_button) {
            performButtonClick(SELECT_GIF);
        }

        if (v.getId() == R.id.take_camera_button) {
            performButtonClick(SELECT_CAMERA);
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

        if (reqCode == SELECT_GIF) {
            intent = new Intent();
            intent.setAction("android.intent.action.GET_CONTENT");
            intent.setType("image/gif");
        }

        if (reqCode == SELECT_AUDIO) {
            intent = new Intent();
            intent.setAction("android.intent.action.GET_CONTENT");
            intent.setType("audio/*");
        }

        if (reqCode == SELECT_CAMERA) {
            //                intent = new Intent(getContext(), CameraActivity.class);
            //                Intent intent2 = this.mActivity.getIntent();
            //                if (intent2.hasExtra(NotificationUtil.CAMERA_STICKER_ID)) {
            //                    intent.putExtra(NotificationUtil.CAMERA_STICKER_ID, intent2.getLongExtra
            // (NotificationUtil.CAMERA_STICKER_ID, -1));
            //                    intent2.removeExtra(NotificationUtil.CAMERA_STICKER_ID);
            //                }
        }

        if (intent != null) {
            startActivityForResult(intent, reqCode);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        handleActivityResult(requestCode, data);
    }


    ViewStub mPrevStub;
    ImageView mPrevImageView;
    ImageView mPrevTypeImageView;

    private void handleActivityResult(int requestCode, Intent data) {
        if (mPrevStub == null) {
            mPrevStub = findViewById(R.id.media_preview_viewstub);
            View rootView = mPrevStub.inflate();

            mPrevImageView = rootView.findViewById(R.id.media_prev_img);
            mPrevTypeImageView = rootView.findViewById(R.id.media_prev_type_img);
        }

        if ( mPrevImageView != null && data != null ) {
            if ( requestCode == SELECT_IMAGE ) {
                Picasso.with(this).load(data.getData()).into(mPrevImageView);
            } else if (requestCode == SELECT_VIDEO ) {
//                String[] filePathColumn = {MediaStore.Images.Media.DATA};
//                Cursor cursor = getContentResolver().query(data.getData(), filePathColumn, null, null, null);
//                cursor.moveToFirst();
//                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//                String picturePath = cursor.getString(columnIndex);
//                cursor.close();
//
//                Bitmap thumb = ThumbnailUtils.createVideoThumbnail(picturePath, MediaStore.Video.Thumbnails.MICRO_KIND) ;

                mPrevTypeImageView.setImageResource(R.drawable.ic_compose_video);
                mPrevTypeImageView.setVisibility(View.VISIBLE);

                Bitmap thumb = getVideoThumb(this.getApplicationContext(), data.getData()) ;
                mPrevImageView.setImageBitmap(thumb);
            }
        }
    }


    public static Bitmap getVideoThumb(Context context, Uri uri) {
        MediaMetadataRetriever mediaMetadataRetriever = null;
        Bitmap bitmap = null;
        try {
            mediaMetadataRetriever = new MediaMetadataRetriever();
            mediaMetadataRetriever.setDataSource(context, uri);
            bitmap = mediaMetadataRetriever.getFrameAtTime();
            if (mediaMetadataRetriever != null) {
                mediaMetadataRetriever.release();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (mediaMetadataRetriever != null) {
                mediaMetadataRetriever.release();
            }
        }
        return bitmap;
    }
}
