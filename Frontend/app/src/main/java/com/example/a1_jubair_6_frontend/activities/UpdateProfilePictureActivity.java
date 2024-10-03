package com.example.a1_jubair_6_frontend.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
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

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.example.a1_jubair_6_frontend.constants.AppConstants;
import com.example.a1_jubair_6_frontend.managers.ProfileDataManager;
import com.example.a1_jubair_6_frontend.R;
import com.example.a1_jubair_6_frontend.network.VolleySingleton;
import com.example.a1_jubair_6_frontend.utils.ImageUtils;

import org.json.JSONObject;

public class UpdateProfilePictureActivity extends AppCompatActivity {

    ImageView profilePicture;
    ImageView backArrow;
    private static final int REQUEST_PERMISSION = 100;
    private Uri selectedImageUri;
    private ProfileDataManager profileDataManager;

    private final ActivityResultLauncher<Intent> getContentLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        selectedImageUri = data.getData();
                        if (selectedImageUri != null) {
                            Glide.with(this)
                                    .load(selectedImageUri)
                                    .circleCrop()
                                    .into(profilePicture);
                            saveResultAndFinish();
                        }
                    }
                }
            });

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
        String currentPictureUriString = getIntent().getStringExtra("currentPictureUri");
        if (currentPictureUriString != null) {
            Uri currentPictureUri = Uri.parse(currentPictureUriString);
            Glide.with(this)
                    .load(currentPictureUri)
                    .circleCrop()
                    .into(profilePicture);
        }
    }

    private void checkPermissionAndShowOptions() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_MEDIA_IMAGES}, REQUEST_PERMISSION);
            }
            else{
                showImageOptions();
            }
        }
        else{
            showImageOptions();
        }
    }

    private void setupClickListeners() {
        backArrow.setOnClickListener(v -> finish());

        findViewById(R.id.btnUpdateProfile).setOnClickListener(v ->
                checkPermissionAndShowOptions());
    }

    private void showImageOptions() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update Profile Picture")
                .setItems(new String[]{"Upload from Library", "Take Photo"}, (dialog, which) -> {
                    switch (which){
                        case 0:
                            openGallery();
                            break;
                        case 1:
                            //TODO take photo
                            break;
                    }
                })
                .show();
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        getContentLauncher.launch(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_PERMISSION) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                showImageOptions();
            }
            else {
                //TODO if permission is denied, maybe show an error to user??
            }
        }
    }

    private void saveResultAndFinish() {
        if (selectedImageUri != null) {
            // Save the URI to SharedPreferences
            profileDataManager.saveProfileImageUri(selectedImageUri);

            //Upload to Server
            uploadProfilePictureToServer(selectedImageUri);

            // Return the result to the calling fragment
            Intent resultIntent = new Intent();
            resultIntent.putExtra("imageUri", selectedImageUri.toString());
            setResult(RESULT_OK, resultIntent);
        }
        finish();
    }

    private void uploadProfilePictureToServer(Uri imageUri) {
        try {
            byte[] imageData = ImageUtils.uriToByteArray(this, imageUri);

            String base64Image = Base64.encodeToString(imageData, Base64.DEFAULT);

            JSONObject jsonBody = new JSONObject();
            jsonBody.put("profile_picture", base64Image);
            jsonBody.put("email", profileDataManager.getEmail());

            String requestUrl = AppConstants.ALEX_POSTMAN_URL + "/postProfilePicture";

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    requestUrl,
                    jsonBody,
                    response -> {
                        Toast.makeText(this, "Upload Sucessful", Toast.LENGTH_SHORT).show();
                        profileDataManager.saveProfileImageUri(imageUri);
                    },
                    error -> {
                        Toast.makeText(this, "Upload Failed", Toast.LENGTH_SHORT).show();
                        Log.e("Upload Failed", error.getMessage());
                    }
            );

            VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error processing image", Toast.LENGTH_SHORT).show();
        }
    }
}