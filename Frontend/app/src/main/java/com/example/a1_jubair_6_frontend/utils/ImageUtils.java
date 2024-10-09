package com.example.a1_jubair_6_frontend.utils;

import android.content.ContentProvider;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ImageUtils {
    private static final String TAG = "ImageUtil";
    private static final int MAX_IMAGE_DIMENSION = 1024;
    private static final int COMPRESSION_QUALITY = 80;

    public static String processAndEncodeImage(Context context, Uri imageUri) throws Exception {
        InputStream inputStream = context.getContentResolver().openInputStream(imageUri);

        BitmapFactory.Options boundsOptions = new BitmapFactory.Options();
        boundsOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(inputStream, null, boundsOptions);
        inputStream.close();

        int sampleSize = calculateSampleSize(boundsOptions);

        inputStream = context.getContentResolver().openInputStream(imageUri);
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = sampleSize;
        Bitmap originalBitmap = BitmapFactory.decodeStream(inputStream, null, bitmapOptions);
        inputStream.close();

        if (originalBitmap == null) {
            throw new Exception("Failed to decode image");
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        originalBitmap.compress(Bitmap.CompressFormat.JPEG, COMPRESSION_QUALITY, outputStream);

        byte[] imageBytes = outputStream.toByteArray();
        String base64Image = Base64.encodeToString(imageBytes, Base64.DEFAULT);

        Log.d(TAG, "Base64 string length: " + base64Image.length());

        outputStream.close();
        originalBitmap.recycle();

        return base64Image;
    }

    private static int calculateSampleSize(BitmapFactory.Options options) {
        int height = options.outHeight;
        int width = options.outWidth;
        int sampleSize = 1;

        while (height > MAX_IMAGE_DIMENSION || width > MAX_IMAGE_DIMENSION) {
            height /= 2;
            width /= 2;
            sampleSize *= 2;
        }

        return sampleSize;
    }
}
