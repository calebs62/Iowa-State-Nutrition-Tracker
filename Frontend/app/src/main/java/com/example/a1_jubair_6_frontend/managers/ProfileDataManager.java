package com.example.a1_jubair_6_frontend.managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;

public class ProfileDataManager {
    private static final String PREF_NAME = "ProfilePreferences";
    private static final String KEY_PROFILE_IMAGE_URI = "profile_image_uri";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";

    private final SharedPreferences preferences;

    public ProfileDataManager(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveProfileImageUri(Uri uri) {
        preferences.edit()
                .putString(KEY_PROFILE_IMAGE_URI, uri.toString())
                .apply();
    }

    public Uri getProfileImageUri() {
        String uriString = preferences.getString(KEY_PROFILE_IMAGE_URI, null);
        return uriString != null ? Uri.parse(uriString) : null;
    }

    public void saveEmailAndPassword(String email, String password){
        preferences.edit()
                .putString(KEY_EMAIL, email)
                .apply();
        preferences.edit()
                .putString(KEY_PASSWORD, password)
                .apply();
    }

    public String getEmail(){
        String email =  preferences.getString(KEY_EMAIL, null);
        return email != null ? email : "";
    }

    public String getPassword(){
        String password = preferences.getString(KEY_PASSWORD, null);
        return password != null ? password : "";
    }
}