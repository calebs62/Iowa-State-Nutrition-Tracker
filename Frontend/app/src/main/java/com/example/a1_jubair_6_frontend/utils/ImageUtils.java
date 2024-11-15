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

/**
 * Utility class for processing and encoding images.
 * Provides functionality to resize, compress, and encode images to Base64 format
 * while maintaining reasonable memory usage and image quality.
 *
 * @author Alexander Svobodny, Caleb Sanchez
 */
public class ImageUtils {
    /** Tag for logging purposes */
    private static final String TAG = "ImageUtil";

    /** Maximum allowed dimension (width or height) for processed images */
    private static final int MAX_IMAGE_DIMENSION = 1024;

    /** JPEG compression quality (0-100) for processed images */
    private static final int COMPRESSION_QUALITY = 80;

    /**
     * Processes an image from a URI and converts it to a Base64 encoded string.
     * This method performs the following operations:
     * <ul>
     *     <li>Loads the image from the provided URI</li>
     *     <li>Calculates appropriate sample size to resize the image</li>
     *     <li>Decodes the image with the calculated sample size</li>
     *     <li>Compresses the image to JPEG format</li>
     *     <li>Converts the compressed image to Base64 string</li>
     * </ul>
     *
     * @param context The application context needed to access content resolver
     * @param imageUri The URI of the image to process
     * @return A Base64 encoded string representation of the processed image
     * @throws Exception If image processing or encoding fails
     */
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

    /**
     * Calculates the appropriate sample size for image downsampling.
     * The sample size is calculated to ensure that the loaded image's dimensions
     * do not exceed MAX_IMAGE_DIMENSION while maintaining aspect ratio.
     * The sample size is always a power of 2.
     *
     * @param options BitmapFactory.Options containing the original image dimensions
     * @return The calculated sample size (power of 2) for image downsampling
     */
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
