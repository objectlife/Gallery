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

package com.weshare.tasks;

/**
 * Created by mrsimple on 14/4/2018.
 */


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.widget.Toast;

import com.weshare.CameraActivity;
import com.weshare.compose.BuildConfig;
import com.weshare.compose.R;
import com.weshare.utils.IOUtils;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * save picture task
 */
public class SaveTask extends Thread {

    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyyMMddHHmmss");

    final WeakReference<CameraActivity> mActivityRef ;
    final Bitmap mBitmap ;
    private int mJpgQuality = 80 ;

    public SaveTask(CameraActivity activity, Bitmap bitmap) {
        this(activity, bitmap, 80);
    }


    public SaveTask(CameraActivity activity, Bitmap bitmap, int quality) {
        mActivityRef = new WeakReference<>(activity);
        mBitmap = bitmap ;
        mJpgQuality = quality;
    }



    @Override
    public void run() {
        final CameraActivity activity = mActivityRef.get() ;
        if ( mActivityRef.get() == null || mActivityRef.get().isFinishing() ) {
            return;
        }

        //create a file to write bitmap data
        Date currentTime = Calendar.getInstance().getTime();
        String fileName = activity.getString(R.string.app_name) + SIMPLE_DATE_FORMAT.format(currentTime) + ".jpg" ;
        final String filePath = bitmapToFile(mBitmap, fileName, mJpgQuality);

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activity.destroyStickerDrawingCache();

                if (TextUtils.isEmpty(filePath)) {
                    Toast.makeText(activity, "Save image file failed :(",
                            Toast.LENGTH_SHORT).show();
                } else {
                    notifyGallery(activity, filePath);
                }
            }
        });
    } // end of run


    private static String bitmapToFile(Bitmap bitmap, String fileName, int quality) {
        File file = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
                fileName);
        try {
            file.createNewFile();
        } catch (Exception e) {
            if (BuildConfig.DEBUG) e.printStackTrace();
            return "";
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, bos);
        byte[] bitmapData = bos.toByteArray();

        IOUtils.closeSilently(bos);

        OutputStream fos = null;
        try {
            fos = new BufferedOutputStream(new FileOutputStream(file));
            fos.write(bitmapData);
            fos.flush();
        } catch (Exception e) {
            if (BuildConfig.DEBUG) e.printStackTrace();
            return "";
        } finally {
            IOUtils.closeSilently(bos);
        }
        return file.getAbsolutePath();
    }

    private void notifyGallery(Context context, String filePath) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File file = new File(filePath);
        Uri contentUri = Uri.fromFile(file);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);

        Toast.makeText(context, "Save image file to " + filePath,
                Toast.LENGTH_SHORT).show();
    }
}
