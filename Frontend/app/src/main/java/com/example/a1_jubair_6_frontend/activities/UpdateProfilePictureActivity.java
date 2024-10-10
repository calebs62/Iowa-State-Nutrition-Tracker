package com.example.a1_jubair_6_frontend.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.example.a1_jubair_6_frontend.constants.AppConstants;
import com.example.a1_jubair_6_frontend.managers.ProfileDataManager;
import com.example.a1_jubair_6_frontend.R;
import com.example.a1_jubair_6_frontend.network.VolleySingleton;
import com.example.a1_jubair_6_frontend.utils.ImageUtils;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class UpdateProfilePictureActivity extends AppCompatActivity {
    private static final String TAG = "UpdateProfilePicture";
    private static final int REQUEST_PERMISSION = 100;
    private static final int MAX_IMAGE_SIZE = 500 * 1024; //500KB

    private ImageView profilePicture;
    private ImageView backArrow;
    private Uri selectedImageUri;
    private ProfileDataManager profileDataManager;


    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if(result.getResultCode() == RESULT_OK && result.getData() != null){
                    handleImageSelection(result.getData().getData());
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile_picture);

        profileDataManager = new ProfileDataManager(this);
        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        backArrow = findViewById(R.id.backArrow);
        profilePicture = findViewById(R.id.ivProfilePic);

        // Load the current profile picture if it exists
        String currentPictureUrl = getIntent().getStringExtra("currentPictureUri");
        if(currentPictureUrl != null && !currentPictureUrl.isEmpty())
            loadProfileImage(currentPictureUrl);
    }

    private void setupClickListeners() {
        backArrow.setOnClickListener(v -> finish());
        findViewById(R.id.btnUpdateProfile).setOnClickListener(v -> checkPermissionAndShowOptions());
    }

    private void checkPermissionAndShowOptions() {
        String permission = Manifest.permission.READ_MEDIA_IMAGES;

        if(ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{permission}, REQUEST_PERMISSION);
        else
            showImageOptions();
    }

    private void showImageOptions() {
        new AlertDialog.Builder(this)
                .setTitle("Update Profile Picture")
                .setItems(new String[]{"Upload from Gallery"}, (dialog, which) -> openGallery())
                .show();
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        galleryLauncher.launch(intent);
    }

    private void handleImageSelection(Uri imageUri){
        if (imageUri != null){
            selectedImageUri = imageUri;
            loadProfileImage(imageUri.toString());
            uploadProfilePicture();
        }
    }

    private void loadProfileImage(String imageUri) {
        Glide.with(this)
                .load(imageUri)
                .circleCrop()
                .placeholder(R.drawable.profile_icon)
                .error(R.drawable.circular_image_background)
                .into(profilePicture);
    }

    private void uploadProfilePicture() {
        try {
            String base64Image = ImageUtils.processAndEncodeImage(this, selectedImageUri);
            if(base64Image == null) return;

            String logPreview = base64Image.length() > 100
                    ? base64Image.substring(0, 100) + "..."
                    : base64Image;

            Log.d(TAG, "Base64 image length: " + base64Image.length() + ", Preview: " + logPreview);

            JSONObject requestBody = new JSONObject();
            requestBody.put("img", base64Image);

            String url = AppConstants.SERVER_URL + "/user/update/" + profileDataManager.getId();

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.PUT,
                    url,
                    requestBody,
                    response -> {
                        Toast.makeText(this, "Profile picture updated sucessfully", Toast.LENGTH_SHORT).show();
                        setSucessResult();
                    },
                    error -> {
                        Log.e(TAG, "Upload error: " + error.toString());
                        Toast.makeText(this, "Failed to update profile picture", Toast.LENGTH_SHORT).show();
                    }
            ){
                @Override
                public Map<String, String> getHeaders(){
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    return headers;
                }
            };

            VolleySingleton.getInstance(this).addToRequestQueue(request);
        } catch (Exception e) {
            Log.e(TAG, "Error preparing upload: " + e.getMessage());
            Toast.makeText(this, "Error preparing image upload", Toast.LENGTH_SHORT).show();
        }
    }

    private void setSucessResult() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("imageUri", selectedImageUri.toString());
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            showImageOptions();
        else
            Toast.makeText(this, "Permission required to access gallery", Toast.LENGTH_SHORT).show();
    }
}