package com.jotangi.cxms.utils;

import static com.jotangi.cxms.utils.BitmapUtils.getBitmapBounds;
import static com.jotangi.cxms.utils.BitmapUtils.getRotateAngleFromImageFile;
import static com.jotangi.cxms.utils.BitmapUtils.loadBitmap;
import static com.jotangi.cxms.utils.BitmapUtils.rotateBitmap;
import static com.realsil.sdk.core.utility.FileUtils.getDataColumn;
import static com.realsil.sdk.core.utility.FileUtils.isDownloadsDocument;
import static com.realsil.sdk.core.utility.FileUtils.isExternalStorageDocument;
import static com.realsil.sdk.core.utility.FileUtils.isMediaDocument;

import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import timber.log.Timber;

public class AppUtils {

    private String TAG = "AppUtils.java";

    public static String logTitle(String str) {
        return "------------------- " + str + " -------------------";
    }

    public static String toHexString(byte[] bytes, int length) {

        StringBuilder hexString = new StringBuilder();

        for (int i = 0; i < length; i++) {

            if (i % 16 == 0 && i != 0) {
                hexString.append("\n");
            }

            String hex = Integer.toHexString(0xFF & bytes[i]);

            if (hex.length() == 1) {
                hexString.append('0' + hex);
            } else {
                hexString.append(hex);
            }

            if (i % 4 == 3) {
                hexString.append(" ");
            }

            hexString.append(" ");
        }

        return "\n" + hexString.toString().toUpperCase();
    }

    public static Bitmap loadAndRotateBitmap(String photoFilePath) {
        try {
            FileInputStream fis = new FileInputStream(photoFilePath);
            int angle = getRotateAngleFromImageFile(fis);
            fis.close();
            fis = new FileInputStream(photoFilePath);
            BitmapFactory.Options bounds = getBitmapBounds(fis);
            fis.close();
            fis = new FileInputStream(photoFilePath);
            Bitmap bmp = loadBitmap(fis);
            fis.close();
            Bitmap rotatebmp = rotateBitmap(bmp, angle, bounds.outWidth, bounds.outHeight);
            return rotatebmp;
        } catch (Exception var6) {
            var6.printStackTrace();
            return null;
        }
    }

    // 圖片壓縮
    public void pictureCutDown(Context context, String path) {

        if (path == null || path.isEmpty()) {
            Timber.w("錯誤 path: " + path);
            return;
        }

        BitmapFactory.Options options = new BitmapFactory.Options();
        // 可避免內存分配
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        int height = options.outHeight;
        int width = options.outWidth;

        if (height == 0 || width == 0) {
            Log.d(TAG, "寬、高 = 0");
            return;
        }

        String mimeType = options.outMimeType;
        Log.d(TAG, String.format("原圖 -> width: %s height: %s mimeType: %s"
                , width, height, mimeType));

        int max;
        if (width > height) {
            max = width;
        } else {
            max = height;
        }

        options.inJustDecodeBounds = false;
        options.inSampleSize = (max / 1000) + 1;
        Bitmap bm = BitmapFactory.decodeFile(path, options);
        Log.d(TAG, String.format("壓縮圖 -> width: %s height: %s size: %s"
                , bm.getWidth(), bm.getHeight(), bm.getByteCount()));
        bmp2Jpg2File(bm,context.getFilesDir() + "/picture.jpeg");
    }

    public void bmp2Jpg2File(Bitmap bitmap, String filePath) {

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);

        try {
            FileOutputStream fos = new FileOutputStream(filePath);
            fos.write(bos.toByteArray());
            fos.flush();
            fos.close();
            Log.d(TAG, "jpg 存檔成功: " + filePath);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "jpg 存檔失敗: " + filePath);
        }
    }

    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }
}
