package com.example.a1_jubair_6_frontend.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;

import androidx.core.content.FileProvider;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageUtils {
    public static byte[] uriToByteArray(Context context, Uri imageUri) throws IOException {
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);

        return byteArrayOutputStream.toByteArray();
    }

    public static Uri base64ToUri(Context context, String base64Image) throws IOException {
        byte[] imageData = Base64.decode(base64Image, Base64.DEFAULT);

        Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);

        File tempFile = File.createTempFile("image", ".jpg", context.getCacheDir());

        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
            fos.flush();
        }

        return FileProvider.getUriForFile(context, context.getPackageName() + ".provider", tempFile);
    }
}
